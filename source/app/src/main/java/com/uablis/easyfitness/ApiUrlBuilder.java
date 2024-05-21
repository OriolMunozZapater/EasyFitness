package com.uablis.easyfitness;


public class ApiUrlBuilder {

    private static String BASE_URL = "http://80.103.131.109:8080/api";

    public ApiUrlBuilder() {

    }

    public String buildUrl(String path) {
        // Concatena la base URL con la ruta específica de la API
        return BASE_URL + "/" + path;
    }
}
