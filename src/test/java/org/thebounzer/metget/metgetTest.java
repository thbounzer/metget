/**
 * Copyright 2012 Carlo Iannaccone

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License
 *
 */

package org.thebounzer.metget;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.thebounzer.metget.Metgetter.airport;

public class metgetTest {


    @Test
    public void metgetTest() throws URISyntaxException, IOException{
        Metgetter metgetter = new Metgetter();
        ArrayList<airport> airports = metgetter.airportsBuilder("Alghero");
        Assert.assertEquals(1, airports.size());
        airport liea = airports.get(0);
        Assert.assertEquals("LIEA", liea.getIcaoCode());
        Assert.assertEquals("AHO", liea.getIataCode());
        Assert.assertTrue(liea.getMetar().contains("Alghero, Italy (LIEA)"));
    }
}
