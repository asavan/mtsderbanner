package mtstest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import simplereport.CsvReport;
import simplereport.SimpleReport;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

public class MtsParserServiceImpl implements MtsParser {
    private static final String BASE_URL = "http://www.shop.mts.ru";
    private static final String BASE_DIR = ".";
    private static final Charset ENCODING = Charset.forName("UTF-8");
    private static final String SEPARATOR = ";";
    private static final String DELIMETR = "\\s*;\\s*";
    private Set<String> categories = new LinkedHashSet<>();
    private final boolean asyncPages;
    private final boolean asyncPhones;
    private final MtsHtmlParser mtsHtmlParser;

    private static final Logger log = Logger.getLogger(MtsParserServiceImpl.class);
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
    public void exit() {
        log.info("Start Exiting...");
        executorServicePhone.shutdown();
        log.info("Exit");
    }


    private SimpleReport generateReport(String name) throws IOException {
        SimpleReport report = new CsvReport(name);
        String[] mainCat = {"Артикул", "Фоточка", "Урл в магазине"};

        Path currentRelativePath = java.nio.file.Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        log.info("Current relative path is: " + s);
        File catFile = new File(BASE_DIR, name + ".txt");
        if (catFile.exists()) {
            String cats = FileUtils.readFileToString(catFile, ENCODING);
            String[] catsFromFile = cats.trim().split(DELIMETR);
            categories.addAll(java.util.Arrays.asList(catsFromFile));
            mainCat = org.apache.commons.lang3.ArrayUtils.addAll(mainCat, catsFromFile);
        }

        report.addRow(mainCat);
        return report;
    }

    private void addRowToReport(SimpleReport report, SmartfonInfo info) {
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
        SimpleReport report = generateReport(typeName);
        int len = categories.size();
        List<Callable<List<String>>> tasks = new ArrayList<>(pages);
        for (int i = 0; i < pages; i++) {
            final String url = BASE_URL + "/" + typeName + "/?PAGEN_1=" + (i + 1);
            Callable<List<String>> task = new Callable<List<String>>() {
                public List<String> call() throws Exception {
                    log.info(url);
                    return mtsHtmlParser.parsePAGENPage(url);
                }
            };
            tasks.add(task);
        }

        processTasks(tasks, asyncPages, completionService, new ListProcessor(report));


        int newLen = categories.size();
        if (newLen != len) {

            log.info("Size not match " + len + " " + newLen);
            report.addRow();
            for (String cat : categories) {
                report.addCell(cat);
            }
            String catAsString = StringUtils.join(categories, SEPARATOR);
            FileUtils.write(new File("./" + typeName + ".txt"), catAsString, ENCODING);
        }

        report.save("./reports");
        log.info(newLen);
        categories = new java.util.LinkedHashSet<>();
        long lEndTime = new Date().getTime();
        long difference = lEndTime - lStartTime;
        log.info("Completed in " + difference / 1000L);
    }

    private interface Processor<T> {
        void process(T value);
    }

    private class ListProcessor implements Processor<List<String>> {

        SimpleReport report;

        ListProcessor(SimpleReport report) {
            this.report = report;
        }

        @Override
        public void process(List<String> value) {
            processUrls(value, report);
        }
    }


    private class OnePageProcessor implements Processor<SmartfonInfo> {

        SimpleReport report;

        OnePageProcessor(SimpleReport report) {
            this.report = report;
        }

        @Override
        public void process(SmartfonInfo value) {
            if (value == null) {
                log.error("Wrong future");
            } else {
                addRowToReport(report, value);
            }
        }
    }

    private static <T> void processTasks(List<Callable<T>> tasks, boolean async, CompletionService<T> completionService, Processor<T> processor) {
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
                processor.process(value);
            } catch (Exception e) {
                log.error("Error...", e);
            }
        }
        log.info("...Done");
    }

    private void processUrls(List<String> urls, SimpleReport report) {
        List<Callable<SmartfonInfo>> tasks = new ArrayList<>(urls.size());
        for (final String smUrl : urls) {
            Callable<SmartfonInfo> task = new Callable<SmartfonInfo>() {
                public SmartfonInfo call() throws IOException {
                    log.debug(smUrl);
                    return mtsHtmlParser.parseOne(smUrl);
                }
            };
            tasks.add(task);
        }

        processTasks(tasks, asyncPhones, completionServicePhone, new OnePageProcessor(report));
    }
}
