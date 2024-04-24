import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Menú de opciones:");
            System.out.println("1. Colombia");
            System.out.println("2. México");
            System.out.println("3. Estados Unidos");
            System.out.println("4. Canadá");
            System.out.println("5. Salir");
            int origen = leerEntero(scanner, "Seleccione el país de origen del dinero: ");

            if (origen == 5) {
                salir = true;
                break;
            }

            int destino = leerEntero(scanner, "Seleccione el país al que desea convertir el dinero: ");

            double cantidad = 0;
            boolean cantidadValida = false;
            while (!cantidadValida) {
                System.out.print("Ingrese la cantidad a convertir: ");
                try {
                    cantidad = scanner.nextDouble();
                    cantidadValida = true;
                } catch (InputMismatchException e) {
                    System.out.println("Debe ingresar un número válido.");
                    scanner.next(); // Limpiar el buffer del scanner
                }
            }

            double resultado = convertirMoneda(origen, destino, cantidad);
            System.out.println("El resultado de la conversión es: " + resultado);
        }

        System.out.println("Gracias por utilizar el conversor de monedas. ¡Hasta luego!");
    }

    private static int leerEntero(Scanner scanner, String mensaje) {
        int valor = 0;
        boolean valorValido = false;
        while (!valorValido) {
            System.out.print(mensaje);
            try {
                valor = scanner.nextInt();
                valorValido = true;
            } catch (InputMismatchException e) {
                System.out.println("Debe ingresar un número válido.");
                scanner.next(); // Limpiar el buffer del scanner
            }
        }
        return valor;
    }

    private static double convertirMoneda(int origen, int destino, double cantidad) {
        double tasaCambio = obtenerTasaCambio(origen, destino);
        return cantidad * tasaCambio;
    }

    private static double obtenerTasaCambio(int origen, int destino) {
        String apiKey = "f40625dc131d238c5bb70134";
        String url = "https://api.exchangerate-api.com/v4/latest/" + getCurrencyCode(origen);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                String responseBody = scanner.useDelimiter("\\A").next();
                scanner.close();

                JSONObject json = JSONUtil.parseObj(responseBody);
                JSONObject rates = json.getJSONObject("rates");

                switch (destino) {
                    case 1: return rates.getDouble("COP");
                    case 2: return rates.getDouble("MXN");
                    case 3: return rates.getDouble("USD");
                    case 4: return rates.getDouble("CAD");
                }
            } else {
                System.out.println("Error al obtener las tasas de cambio. Código de respuesta: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static String getCurrencyCode(int pais) {
        switch (pais) {
            case 1: return "COP";
            case 2: return "MXN";
            case 3: return "USD";
            case 4: return "CAD";
            default: return "";
        }
    }
}
