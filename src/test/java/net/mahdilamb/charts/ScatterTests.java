package net.mahdilamb.charts;

import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.plots.Scatter;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScatterTests {

    static final DataFrame iris = loadData("iris.csv");
    static final DataFrame tips = loadData("tips.csv");

    public static DataFrame loadData(final String resourcePath) {
        return DataFrame.from(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(resourcePath)).getFile()));
    }

    @Test
    public void PlotlyLineAndScatter0() {
        /*
        fig = px.scatter(x=[0, 1, 2, 3, 4], y=[0, 1, 4, 9, 16])
         */
        final Scatter c = new Scatter(new double[]{0, 1, 2, 3, 4}, x -> x * x);
        System.out.println(c);
    }

    @Test
    public void PlotlyLineAndScatter1() {
        /*
        fig = px.scatter(df, x="sepal_width", y="sepal_length")
         */
        final Scatter c = new Scatter(iris, "sepal_width", "sepal_length");
        System.out.println(c);
    }

    @Test
    public void PlotlyLineAndScatter2() {
        /*
      fig = px.scatter(df, x="sepal_width", y="sepal_length", color="species",
                 size='petal_length', hover_data=['petal_width'])
         */
        final Scatter c = new Scatter(iris, "sepal_width", "sepal_length")
                .setColors("species")
                .setSizes("petal_length");
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot0() {
        /*
            sns.scatterplot(data=tips, x="total_bill", y="tip")
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip");
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot1() {
        /*
        sns.scatterplot(data=tips, x="total_bill", y="tip", hue="time")
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("time");
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot2() {
        /*
        sns.scatterplot(data=tips, x="total_bill", y="tip", hue="time", style="time")
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("time")
                .setShapes("time");
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot3() {
        /*
        sns.scatterplot(data=tips, x="total_bill", y="tip", hue="day", style="time")
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("day")
                .setShapes("time");
        System.out.println(c);


    }

    @Test
    public void SeabornScatterPlot4() {
        /*
        sns.scatterplot(data=tips, x="total_bill", y="tip", hue="size")
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("size");
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot5() {
        /*
        sns.scatterplot(data=tips, x="total_bill", y="tip", hue="size", palette="deep")
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("size")
                .setColorMap("deep");
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot7() {
        /*
        sns.scatterplot(data=tips, x="total_bill", y="tip", hue="size", size="size")
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("size")
                .setSizes("size");
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot8() {
        /*
       sns.scatterplot(
            data=tips, x="total_bill", y="tip", hue="size", size="size",
            sizes=(20, 200), legend="full"
        )
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("size")
                .setSizes("size", 20, 200);
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot9() {
        /*
       sns.scatterplot(
            data=tips, x="total_bill", y="tip", hue="size", size="size",
            sizes=(20, 200), hue_norm=(0, 7), legend="full"
        )
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("size", 0, 7)
                .setSizes("size");
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot10() {
        /*
       markers = {"Lunch": "s", "Dinner": "X"}
        sns.scatterplot(data=tips, x="total_bill", y="tip", style="time", markers=markers)
         */
        final Map<String, String> markers = new HashMap<>(2);
        markers.put("Lunch", "s");
        markers.put("Dinner", "X");
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setShapes("time", markers);
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot10Alt() {
        /*
       markers = {"Lunch": "s", "Dinner": "X"}
        sns.scatterplot(data=tips, x="total_bill", y="tip", style="time", markers=markers)
         */

        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setShapes("time", val -> "Lunch".equals(val) ? "s" : "X");
        System.out.println(c);
    }

    @Test
    public void SeabornScatterPlot11() {
        /*
        sns.scatterplot(data=tips, x="total_bill", y="tip", s=100, color=".2", marker="+")
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setShape('+')
                .setColor(".2")
                .setSize(10);

        System.out.println(c);
    }


    @Test
    public void SeabornRelPlot0() {
        /*
        sns.relplot(data=tips, x="total_bill", y="tip", hue="day")
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("day");
        System.out.println(c);
    }

    @Test
    public void SeabornRelPlot1() {
        /*
        sns.relplot(data=tips, x="total_bill", y="tip", hue="day", col="time")
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("day")
                .setCols("time");
        System.out.println(c);
    }

    @Test
    public void SeabornRelPlot2() {
        /*
        sns.relplot(data=tips, x="total_bill", y="tip", hue="day", col="time", row="sex")

         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("day")
                .setCols("time")
                .setRows("sex");
        System.out.println(c);
    }

    @Test
    public void SeabornRelPlot3() {
        /*
        sns.relplot(data=tips, x="total_bill", y="tip", hue="time", col="day", col_wrap=2)

         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("day")
                .setCols("time")
                .setColWrap(2);
        System.out.println(c);
    }

    @Test
    public void SeabornRelPlot4() {
        /*
        sns.relplot(
            data=tips, x="total_bill", y="tip", col="time",
            hue="time", size="size", style="sex",
            palette=["b", "r"], sizes=(10, 100)
        )
         */
        final Scatter c = new Scatter(tips, "total_bill", "tip")
                .setColors("time")
                .setCols("time")
                .setSizes("size", 10, 100)
                .setShapes("sex")
                .setColorMap("b", "r");

        System.out.println(c);
    }
}
