import net.mahdilamb.charts.dataframe.DataFrame;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

public class Tests {
    @Test
    public void CSVTest() {
        final DataFrame iris = DataFrame.from(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("iris.csv")).getFile()));
        System.out.println(iris.getDoubleSeries("petal_length"));
    }

}
