package org.example;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        List<String> rawTempoStrings = new ArrayList<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse("C:/Users/Navajo/Documents/Native Instruments/Traktor 3.7.1/collection.nml"); //TODO Change this for personal use
            NodeList tempoList = doc.getElementsByTagName("TEMPO");
            for (int i = 0; i < tempoList.getLength(); i++) {
                Node tempoNode = tempoList.item(i);
                System.out.println(tempoNode.getNodeType());
                if (tempoNode.getNodeType()==Node.ELEMENT_NODE) {
                    Element tempoElement = (Element) tempoNode;
                    String tempoString = tempoElement.getAttribute("BPM");
                    rawTempoStrings.add(tempoString);
                }
            }
            Map<Integer,Integer> bpmMap = generateBpmMap(rawTempoStrings);
            System.out.println("tempo count/bin count is: " + bpmMap.size());
            double[] bpmValues = generateBpmDoubleArray(rawTempoStrings, tempoList.getLength());
            plotHistogram("Tempos", bpmValues, bpmMap.size(), 50, 190);
        } catch (ParserConfigurationException e) {
            System.out.println("Problem with parser: " + e);
        } catch (IOException e) {
            System.out.println("Problem with parser: " + e);
        } catch (SAXException e) {
            System.out.println("Problem with parser: " + e);
        }

    }

    private static void plotHistogram(String key, double[] bpmValues, int binCount, int minTempo, int maxTempo) throws IOException {
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(key, bpmValues, binCount/3, minTempo, maxTempo);
        JFreeChart histogram = ChartFactory.createHistogram("Frequency of BPM Values in Ben Truchan's DJ Library", //TODO Change this for personal use
                "Tempo/BPM", "Frequency", dataset);

        ChartUtils.saveChartAsPNG(new File("C://Users/Navajo/Desktop/TraktorBpmHistogram.png"), histogram, 600, 400); //TODO Change this for personal use
    }

    private static double[] generateBpmDoubleArray(List<String> rawTempoStrings, int tempoListCount) {
        List<Double> doubleList = new ArrayList<>();
        for (String tempoString : rawTempoStrings) {
            Double bpmDouble = evaluateBpmToDoubleFromString(tempoString);
            Integer bpmInteger = bpmDouble.intValue();
            Double bpmDoubleRounded = bpmInteger.doubleValue();
            doubleList.add(bpmDoubleRounded);
        }
        double[] doubleArray = new double[tempoListCount];
        for (int i = 0; i < tempoListCount; i++) {
            doubleArray[i] = doubleList.get(i);
        }
        return doubleArray;
    }

    private static Double evaluateBpmToDoubleFromString(String tempoString) {
        return Double.parseDouble(tempoString);
    }

    private static Map<Integer, Integer> generateBpmMap(List<String> rawTempoStrings) {
        Map<Integer,Integer> bpmMap = new HashMap<>();
        for (String tempoString : rawTempoStrings) {
            Integer bpmInteger = evaluateBpmFromString(tempoString);
            if (!bpmMap.containsKey(bpmInteger)) {
                bpmMap.put(bpmInteger, 1);
            }
            else {
                bpmMap.put(bpmInteger, bpmMap.get(bpmInteger)+1);
            }
        }
        return bpmMap;
    }

    private static Integer evaluateBpmFromString(String tempoString) {
        Double tempoDouble = Double.parseDouble(tempoString);
        return tempoDouble.intValue();
    }
}