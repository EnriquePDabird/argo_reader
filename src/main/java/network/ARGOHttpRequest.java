package network;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ARGOHttpRequest {

    public HttpResponse<String> connectionRequestTester(){
        HttpClient defaultClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(10))
        .build();
        
        String urlAtlanticOcean2025 = "https://data-argo.ifremer.fr/geo/atlantic_ocean/2025/";

        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(urlAtlanticOcean2025))
        .GET()
        .build();

        try{
            HttpResponse<String> response = defaultClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if(response.statusCode() >= 200 && response.statusCode() < 300){
                return response;
            } else {
                System.err.println("La solicitud falló con el código de estado: " + response.statusCode());
                return response;
            }
        } catch (IOException | InterruptedException e) {
            // La solicitud no pudo completarse (timeout, no hay internet, error de DNS)
            System.err.println("Error al enviar la solicitud " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args){
        ARGOHttpRequest tester = new ARGOHttpRequest();
        System.out.println(tester.connectionRequestTester().body());
    }
}