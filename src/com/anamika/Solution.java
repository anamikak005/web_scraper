package com.anamika;

import java.io.*;

import org.json.*;
import org.jsoup.Jsoup;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Anamika
 */

public class Solution {

    /**
     * @param args the command line arguments
     */
    private static List<JSONObject> list = new ArrayList<>();

    static void mm(Element child2) throws IOException, JSONException {

        ExecutorService executor = Executors.newFixedThreadPool(30);
        Elements children3 = child2.children();
        for (Element child3 : children3) {
            Elements children4 = child3.select("a[href]");
            for (Element child4 : children4) {

                Runnable worker = new MyRunnable(child4);
                executor.execute(worker);

            }
        }
        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {

        }


    }

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://www.cermati.com/karir").get();

        Map<String, List> finalMap = new HashMap<>();
        String filename = "solution.json";

        Elements links = doc.select("div[class~=tab-content]");
        String s = "";
        for (Element link : links) {

            Elements children = link.children();
            for (Element child : children) {
                if (child.hasClass("tab-pane") || child.hasClass("tab-pane active")) {
                    Elements children2 = child.children();
                    for (Element child2 : children2) {

                        if (child2.hasClass("dept-label")) {
                            s = child2.text();

                        }
                        //Department of job
                        if (child2.hasClass("container-fluid")) {


                            mm(child2);

                            List<JSONObject> list2 = new ArrayList<>();
                            for (JSONObject j : list) {
                                list2.add(j);
                            }
                            finalMap.put(s, list2);
                            list.clear();


                        }
                    }


                }

            }


        }
        JSONObject finalJSON = new JSONObject(finalMap);


        try (FileWriter file = new FileWriter(filename)) {
            file.write(finalJSON.toString(2));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


    }

    public static class MyRunnable implements Runnable {
        private final Element child4;

        MyRunnable(Element child4) {
            this.child4 = child4;
        }

        @Override
        public void run() {
            JSONObject temp2 = new JSONObject();
            //Each job inside dept
            String sub_doc = child4.attr("href");
            try {


                Document doc2 = Jsoup.connect(sub_doc).get();
                //Job title
                Elements s_l_title = doc2.select("h1[class~=job-title]");
                temp2.put("title", s_l_title.text());
//
                //job location
                Elements s_l_loc = doc2.select("span[class~=job-detail]");
                //System.out.println(s_l_loc.text());
                temp2.put("Location", s_l_loc.text());
//                               //Description
                Elements s_l_Desc = doc2.select("div[itemprop~=responsibilities]");
                List<String> arr1 = new ArrayList<>();
                // System.out.println(s_l_Desc.text());
                Elements pp = s_l_Desc.select("li");
                for (Element p : pp) {
                    arr1.add(p.text());
                    //System.out.println(p.text());

                }
                JSONArray jsonArray = new JSONArray(arr1);
                temp2.put("Description", jsonArray);
                //  generator_sub.writeEndArray();

                //Qualifications
                Elements s_l_Qual = doc2.select("div[itemprop~=qualifications]");

                Elements p1 = s_l_Qual.select("li");
                List<String> arr = new ArrayList<>();
                for (Element p : p1) {
                    arr.add(p.text());

                }
                JSONArray jsonArray1 = new JSONArray(arr);
                temp2.put("Qualification", jsonArray1);

                //posted by

                Elements p_b_title = doc2.select("h3[class~=details-title]");

                temp2.put("Posted By", p_b_title.text());

                list.add(temp2);

            } catch (IOException | JSONException e) {

            }

        }
    }


}
