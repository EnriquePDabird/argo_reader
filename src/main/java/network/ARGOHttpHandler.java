package network;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.file.*;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class ARGOHttpHandler {
    private final HttpClient defaultClient;
    private final String urlAtlanticOcean2025 = "https://data-argo.ifremer.fr/geo/atlantic_ocean/2025/";
    private final Path downloadDir = Paths.get("src/main/java/resources");

    public ARGOHttpHandler(){
        this.defaultClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    // Crea las carpetas donde se almacenarán los archivos NetCDF
    private void createFolders(){
        Elements monthLinks = parseMainDirectory();
        System.out.println(monthLinks.size());
        for(Element monthLink : monthLinks){
            String monthHref = monthLink.attr("href");
            String monthDirName = monthHref.replace("/", "");

            Path monthDir = this.downloadDir.resolve(monthDirName);
            try {
                Files.createDirectories(monthDir);
                System.out.println("Carpeta creada (o ya existía): " + monthDir.toAbsolutePath());

            } catch (IOException e) {
                System.err.println("Error al crear la carpeta: " + monthDir.toAbsolutePath());
            }
        }
    }

    // Descarga y almacena los datos .nc en la carpeta correspondiente según su mes
    private void ncdfDownloader(){
        Elements monthLinks = parseMainDirectory();
        for(Element monthLink : monthLinks){
            String monthFullURL = urlAtlanticOcean2025 + monthLink.attr("href");

        }
    }

    private Elements parseMainDirectory(){
        String htmlBody = getHTMLContent(urlAtlanticOcean2025);
        if(htmlBody == null){
            System.err.println("No se pudo obtener el contenido de la URL base");
        }

        Document doc = Jsoup.parse(htmlBody, urlAtlanticOcean2025);
        Elements monthLinks = doc.select("a[href~=^\\d{2}/$]");
        return monthLinks;
    }

    // Devuelve el cuerpo de la petición HTML, método base
    private String getHTMLContent(String url){
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET()
        .build();

        try{
            HttpResponse<String> response = defaultClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if(response.statusCode() >= 200 && response.statusCode() < 300){
                return response.body();
            } else {
                System.err.println("La solicitud falló con el código de estado: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            // La solicitud no pudo completarse (timeout, no hay internet, error de DNS)
            System.err.println("Error al enviar la solicitud " + e.getMessage());
            return null;
        }
    }

    private <T> HttpResponse baseRequestHandler(HttpRequest request, HttpResponse.BodyHandler<T> handler){
        return null;
    }

    public static void main(String[] args){
        ARGOHttpHandler request = new ARGOHttpHandler();
        request.createFolders();
        request.ncdfDownloader();
    }
}
