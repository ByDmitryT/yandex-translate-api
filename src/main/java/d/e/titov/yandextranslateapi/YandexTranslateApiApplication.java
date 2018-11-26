package d.e.titov.yandextranslateapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Scanner;

public class YandexTranslateApiApplication {

	private static final String YANDEX_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";
	private static final String API_KEY = "trnsl.1.1.20181126T154655Z.de88e6e10aa7845c.1542b7fa9824e93d0a1851c76b1860fe761b4f37";

	private static String translate(String text) {
		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			URIBuilder uriBuilder = new URIBuilder(YANDEX_URL);
			uriBuilder.setParameter("lang", "en-ru").setParameter("key", API_KEY);
			HttpPost request = new HttpPost(uriBuilder.build());
			request.setHeader("Content-Type", "application/x-www-form-urlencoded");
			request.setEntity(new StringEntity(String.format("text=%s", text)));

			HttpResponse response = client.execute(request);
			BufferedReader buf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuilder builder = new StringBuilder();
			String line;

			while ((line = buf.readLine()) != null) {
				builder.append(line);
				builder.append(System.lineSeparator());
			}

			return builder.toString();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String parseResponse(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		StringBuilder builder = new StringBuilder();
		try {
			JsonNode rootNode = objectMapper.readTree(json);
			JsonNode textNode = rootNode.path("text");
			Iterator<JsonNode> elements = textNode.elements();
			while (elements.hasNext()) {
				JsonNode text = elements.next();
				builder.append(text.asText());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	public static void main(String[] args) {
		System.out.println("Program is running");
		while (true) {
			Scanner scanner = new Scanner(System.in);
			String line = scanner.nextLine();
			System.out.println(parseResponse(translate(line)));
		}
	}
}
