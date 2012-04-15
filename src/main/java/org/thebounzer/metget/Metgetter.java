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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;

public class Metgetter 
{
    private final String tmpDir = System.getProperty("java.io.tmpdir");
    private final String separator = System.getProperty("file.separator");
    private final String ftpHost = "tgftp.nws.noaa.gov";
    private final String ftpUser = "anonymous";
    private final String ftpPassword = "anonymous";
    private final String ftpPath = "data/observations/metar/decoded/";
    private final String ftpTempName = "out";
    private final String noResultFound = "<h3 align=\"center\">Sorry-No Results Found</h1><br/>";

    public class airport
    {
        private String name;
        private String country;
        private String location;
        private String icaoCode;
        private String iataCode;
        private String faaCode;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getFaaCode() {
            return faaCode;
        }

        public void setFaaCode(String faaCode) {
            this.faaCode = faaCode;
        }

        public String getIataCode() {
            return iataCode;
        }

        public void setIataCode(String iataCode) {
            this.iataCode = iataCode;
        }

        public String getIcaoCode() {
            return icaoCode;
        }

        public void setIcaoCode(String icaoCode) {
            this.icaoCode = icaoCode;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMetar() throws SocketException, IOException{
            if ( !(icaoCode.contains("&nbsp")) || !(icaoCode.isEmpty()) || !(icaoCode != null)){
                FTPClient client = new FTPClient();
                client.connect(ftpHost);
                client.login(ftpUser, ftpPassword);
                client.changeWorkingDirectory(ftpPath);
                File out = new File(tmpDir+separator+ftpTempName);
                out.createNewFile();
                OutputStream output = new FileOutputStream(out);
                client.retrieveFile(icaoCode+".TXT", output);
                client.disconnect();
                System.out.println("################ METAR FOR "+icaoCode+" ################");
                BufferedReader reader = new BufferedReader(new FileReader(out));
                String input;
                String metar ="";
                while((input = reader.readLine())!= null){
                    System.out.println(input);
                    metar = metar+input;
                }
                out.delete();
                return metar;
            }else{
                return "No valid ICAO code!";
            }

        }
    }
        
    public static void main( String[] args ) throws URISyntaxException, IOException
    {
        Metgetter metgetter = new Metgetter();
        for (String name : args){
            ArrayList<airport> airports = metgetter.airportsBuilder(name);
            for(airport airp : airports){
                System.out.println("Airport name: "+airp.getName()+" and ICAO-Code is :"+airp.icaoCode);
                airp.getMetar();
            }
        }
    }
    
    public ArrayList<airport> airportsBuilder(String airportname) throws URISyntaxException, IOException{;
        String postParams = "airport="+airportname.replace(" ", "+")+"&but1=submit";
        URI uri = URIUtils.createURI("http", "www.airlinecodes.co.uk", -1, "/aptcoderes.asp", postParams, null);
        ArrayList<String> tables = new ArrayList<String>();
        HttpPost post = new HttpPost(uri);
        System.out.println("Get:"+post.getURI());
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse resp = httpclient.execute(post);
        InputStream inp = resp.getEntity().getContent();
        Reader reader = new InputStreamReader(inp);
        BufferedReader buffered = new BufferedReader(reader);
        String input;
        while((input = buffered.readLine())!= null){
            tables.addAll(HTMLSimpleParser("<table ", "</table>", input));
            if (input.contains(noResultFound)) {
                throw new IOException("Airport "+airportname+" not found!");
            }
        }
        return extractRows(tables);
    }

    private ArrayList<String> HTMLSimpleParser(String tagOpen, String tagClose, String html){
        ArrayList<String> elements = new ArrayList<String>();
        while( (html.indexOf(tagOpen) != -1) && (html.indexOf(tagClose)!= -1)){
            elements.add(html.substring(html.indexOf(tagOpen),html.indexOf(tagClose)+tagClose.length()));
            html = html.substring(html.indexOf(tagClose)+tagClose.length());
        }
        return elements;
    }
    
    private String HTMLTagRemover(String input){
        while( (input.indexOf("<") != -1) && (input.indexOf(">") != -1) ){
            input = input.substring(input.indexOf(">")+1,input.lastIndexOf("<"));
        }
        return input;
    }

    private ArrayList<airport> extractRows(ArrayList<String> tables){
        ArrayList<String> dataRows = new ArrayList<String>();
        ArrayList<airport> airports = new ArrayList<airport>();
        String plainrow ="";
        for(String table : tables){
            ArrayList<String> rows = HTMLSimpleParser("<tr>", "</tr>", table);
            for(String row: rows){
                ArrayList<String> dataRow = (HTMLSimpleParser("<td", "</td>", row));
                for (String dataR : dataRow){
                    plainrow = plainrow+HTMLTagRemover(dataR);
                }
                dataRows.add(plainrow);
                plainrow ="";
            }
            airports.add(createAirport(dataRows));
            dataRows = new ArrayList<String>();
        }
        return airports;
    }

    private airport createAirport(ArrayList<String> extractedData){
        airport airp = new airport();
        for(String row : extractedData){
            if (row.contains("Location:")){
                airp.setLocation(row.replace("Location:", ""));
            }else if (row.contains("IATA-Code:")){
                airp.setIataCode(row.replace("IATA-Code:", ""));
            }else if (row.contains("ICAO-Code:")){
                airp.setIcaoCode(row.replace("ICAO-Code:", ""));
            }else if (row.contains("FAA-Code:")){
                airp.setFaaCode(row.replace("FAA-Code:", ""));
            }else if (row.contains("Airport:")){
                airp.setName(row.replace("Airport:", ""));
            }else if (row.contains("Country:")){
                airp.setCountry(row.replace("Country:", ""));
            }
        }
        return airp;
    }
}
