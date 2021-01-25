package net.mahdilamb.charts;

import net.mahdilamb.charts.series.Dataset;
import org.junit.Test;

import java.io.File;

public class DatasetTests {
    @Test
    public void fromCSVTest() {
        System.out.println(Dataset.importer(new File("D:\\mahdi\\Desktop\\train.csv")).build());
    }
}
