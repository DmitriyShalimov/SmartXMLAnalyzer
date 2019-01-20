package com.agileengine;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Wrong arguments, usage:  <input_origin_file_path> <input_other_sample_file_path> ");
            System.exit(0);
        }
        File originFile=new File(args[0]);
        if (!originFile.exists()) {
            System.err.println("ERROR: Origin file does not exists. Please correct program arguments.");
            System.exit(0);

        }

        File sampleFile=new File(args[1]);
        if (!sampleFile.exists()) {
            System.err.println("ERROR: Sample file does not exists. Please correct program arguments.");
            System.exit(0);
        }
        HtmlFuzzySearch htmlFuzzySearch = new HtmlFuzzySearch();
        String string = htmlFuzzySearch.process(originFile, sampleFile);
        System.out.println(string);
    }
}
