# DataViz

DataViz is a Java library to assist in the exploration of data analysis. It is dependent on the sister package "
DataFrame".

At the moment, DataViz support scatter and line charts, but has the capacity to be further developed to support a broad
range of plot types. A heavy focus has been put on the API to make it as usable as possible. As a result, it uses many
Java8 functional interfaces to reduce boiler-plate code for users.

The package also supports input and export (e.g. to SVG, and a number of raster types).

# Example code

## Scatter chart from a CSV

```java
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.plots.Scatter;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        DataFrame iris = DataFrame.from(new File("iris.csv"));
        new Scatter(iris, "petal_length", "petal_width")
                .show();
    }
}
```

### Example customization

A number of attributes can be used to change the appearance of the scatter chart

```java
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.plots.Scatter;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        DataFrame iris = DataFrame.from(new File("iris.csv"));
        new Scatter(iris, "petal_length", "petal_width")
                .setSizes("petal_length")
                .setColors("species")
                .updateLegend(legend -> {
                    legend.setSide(Side.LEFT);
                })
                .show();
    }
}
```

Here, the side of the legend has also been moved to the right using a lambda function

## Attributions

* The Rtree implementation is heavily based on that from [mourner's r-bush](https://github.com/mourner/rbush), which is
  released under the MIT licence
* [Ryu](https://github.com/ulfjack/ryu) is used under the Apache v2.0 licence to assist in displaying floating point
  numbers
* Computing arcs in Swing/AWT in a way that is consistent with the SVG specification is performed using a method from
  Batik, which is also released under Apache v2.0
* [Material icons](https://github.com/google/material-design-icons) have been used for icon buttons and a released by
  Google under Apache v2.0

## Licence

Copyright (C) 2020-2021 Mahdi Lamb, University of Edinburgh

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.