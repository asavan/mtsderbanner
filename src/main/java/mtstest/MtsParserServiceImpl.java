package mtstest;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simplereport.CsvReport;
import simplereport.SimpleReport;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MtsParserServiceImpl implements MtsParser {
    private static final String BASE_URL = "http://www.shop.mts.ru";
    private static final String BASE_DIR = ".";
    private static final Charset ENCODING = StandardCharsets.UTF_8;
    private static final String SEPARATOR = ";";
    private static final String DELIMITER = "\\s*;\\s*";
    private static final String[] DEFAULT_CATEGORIES = {"Артикул", "Фоточка", "Урл в магазине"};
    private final boolean asyncPages;
    private final boolean asyncPhones;
    private final MtsHtmlParser mtsHtmlParser;

    private static final Logger log = LogManager.getLogger(MtsParserServiceImpl.class);
    private final CompletionService<List<String>> completionService;
    private final CompletionService<SmartfonInfo> completionServicePhone;
    private final ExecutorService executorServicePhone;


    public MtsParserServiceImpl(boolean asyncPages, boolean asyncPhones, int nThreadsPhones) {
        this.asyncPages = asyncPages;
        this.asyncPhones = asyncPhones;
        if (asyncPages || asyncPhones) {
            executorServicePhone = Executors.newFixedThreadPool(nThreadsPhones);
        } else {
            executorServicePhone = null;
        }
        if (asyncPages) {
            completionService = new ExecutorCompletionService<>(executorServicePhone);
        } else {
            completionService = null;
        }
        if (asyncPhones) {
            completionServicePhone = new ExecutorCompletionService<>(executorServicePhone);
        } else {
            completionServicePhone = null;
        }
        mtsHtmlParser = new MtsHtmlParserImpl();
    }

    @Override
    public void exit() throws InterruptedException {
        log.info("Start Exiting...");
        executorServicePhone.shutdown();
        executorServicePhone.awaitTermination(5, TimeUnit.HOURS);
        log.info("Exit");
    }


    private static SimpleReport generateReport(String name, Set<String> categories) throws IOException {
        SimpleReport report = new CsvReport(name);
        List<String> mainCat = new ArrayList<>(Arrays.asList(DEFAULT_CATEGORIES));

        Path currentRelativePath = java.nio.file.Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        log.info("Current relative path is: " + s);
        File catFile = getCategoriesFile(name);
        if (catFile.exists()) {
            String cats = FileUtils.readFileToString(catFile, ENCODING);
            String[] catsFromFile = cats.trim().split(DELIMITER);
            List<String> catList = Arrays.asList(catsFromFile);
            categories.addAll(catList);
            mainCat.addAll(catList);
        }

        report.addCaptionRow(mainCat);
        return report;
    }

    private static File getCategoriesFile(String name) {
        return new File(BASE_DIR, name + ".txt");
    }

    private static void addRowToReport(SimpleReport report, SmartfonInfo info, Set<String> categories) {
        categories.addAll(info.getProperties().keySet());
        report.addRow();
        report.addCell(info.getArticul());
        report.addCell(info.getMainPhoto());
        report.addCell(info.getOriginalUrl());
        for (String category : categories) {
            String value = info.getProperties().get(category);
            if (value == null) {
                log.debug(category);
                log.debug(info.getOriginalUrl());
                report.skipCell();
            } else {
                report.addCell(value);
            }
        }
    }

    @Override
    public void parseList(String typeName, int pages) throws IOException {
        long lStartTime = new Date().getTime();
        Set<String> categories = new LinkedHashSet<>();
        SimpleReport report = generateReport(typeName, categories);
        int len = categories.size();
        List<Callable<List<String>>> tasks = new ArrayList<>(pages);
        for (int i = 0; i < pages; i++) {
            final String url = BASE_URL + "/" + typeName + "/?PAGEN_1=" + (i + 1);
            Callable<List<String>> task = () -> mtsHtmlParser.parsePAGENPage(url);
            tasks.add(task);
        }

        processTasks(tasks, asyncPages, completionService, new ListProcessor(report, categories));


        int newLen = categories.size();
        if (newLen != len) {

            log.info("Size not match " + len + " " + newLen);
            report.addRow();
            for (String cat : categories) {
                report.addCell(cat);
            }
            String catAsString = String.join(SEPARATOR, categories);
            FileUtils.writeStringToFile(getCategoriesFile(typeName), catAsString, ENCODING);
        }

        report.save("./reports");
        log.info(newLen);
        long lEndTime = new Date().getTime();
        long difference = lEndTime - lStartTime;
        log.info("Completed in " + difference / 1000L);
    }

    private class ListProcessor implements Consumer<List<String>> {

        private final SimpleReport report;
        private final Set<String> categories;


        ListProcessor(SimpleReport report, Set<String> categories) {
            this.report = report;
            this.categories = categories;
        }

        @Override
        public void accept(List<String> value) {
            processUrls(value, new OnePageProcessor(report, categories));
        }
    }


    private class OnePageProcessor implements Consumer<SmartfonInfo> {

        private final SimpleReport report;
        private final Set<String> categories;

        OnePageProcessor(SimpleReport report, Set<String> categories) {
            this.report = report;
            this.categories = categories;
        }

        @Override
        public void accept(SmartfonInfo value) {
            if (value == null) {
                log.error("Wrong future");
            } else {
                addRowToReport(report, value, categories);
            }
        }
    }

    private static <T> void processTasks(List<Callable<T>> tasks, boolean async, CompletionService<T> completionService, Consumer<T> processor) {
        if (async) {
            for (Callable<T> t : tasks) {
                completionService.submit(t);
            }
        }

        log.info("Process...");
        for (Callable<T> t : tasks) {
            try {
                T value;
                if (async) {
                    value = completionService.take().get();
                } else {
                    value = t.call();
                }
                processor.accept(value);
            } catch (Exception e) {
                log.error("Error...", e);
            }
        }
        log.info("...Done");
    }

    private void processUrls(List<String> urls, Consumer<SmartfonInfo> processor) {
        List<Callable<SmartfonInfo>> tasks2 = urls.stream()
                .map(it -> (Callable<SmartfonInfo>) () -> mtsHtmlParser.parseOne(it))
                .collect(Collectors.toList());

        processTasks(tasks2, asyncPhones, completionServicePhone, processor);
    }
}
