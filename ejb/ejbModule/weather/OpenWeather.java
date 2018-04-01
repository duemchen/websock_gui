package weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import de.horatio.common.HoraTime;

/**
 *
 * @author duemchen
 */
public class OpenWeather {

	public static Logger log = Logger.getLogger("OpenWeatherLogger");
	private double lat;
	private double lon;

	public void setCoord(double lon, double lat) {
		this.lat = lat;
		this.lon = lon;

	}

	/**
	 *
	 * @return @throws MalformedURLException
	 * @throws IOException
	 * @throws JSONException
	 */
	public double getWind() throws MalformedURLException, IOException, JSONException {
		double result2 = 0;

		String geourl1 = ("http://api.openweathermap.org/data/2.5/weather?lat=");
		String georul2 = ("&lon=");
		// String geourl3 =
		// ("&appid=aaa67fe700a77d94eb3d115299f40e93&lang=de&units=Metric&temperature.unit=Celsius,");
		String geourl3 = ("&appid=aaa67fe700a77d94eb3d115299f40e93&lang=de&units=Metric");

		StringBuilder urlstr = new StringBuilder(geourl1);
		urlstr.append(lat);
		urlstr.append(georul2);
		urlstr.append(lon);
		urlstr.append(geourl3);
		// System.out.println(urlstr);

		String url2 = urlstr.toString();

		URL url = new URL(url2);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		InputStream stream = connection.getInputStream();
		// System.out.println(stream);
		// read the contents using an InputStreamReader

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		String str;
		str = result.toString();

		// log.info(str);
		JSONObject json2 = new JSONObject(str);

		JSONObject json2Wind = json2.getJSONObject("wind");
		result2 = json2Wind.getDouble("speed");
		// System.out.println("wind:" + result2);
		return result2;

	}

	/**
	 *
	 * @return @throws MalformedURLException
	 * @throws IOException
	 * @throws JSONException
	 */
	public double getTemp() throws MalformedURLException, IOException, JSONException {
		double result = 0;

		String geourl1 = ("http://api.openweathermap.org/data/2.5/weather?lat=");
		String georul2 = ("&lon=");
		// String geourl3 =
		// ("&appid=aaa67fe700a77d94eb3d115299f40e93&lang=de&units=Metric&temperature.unit=Celsius,");
		String geourl3 = ("&appid=aaa67fe700a77d94eb3d115299f40e93&lang=de&units=Metric");

		StringBuilder urlstr = new StringBuilder(geourl1);
		urlstr.append(lat);
		urlstr.append(georul2);
		urlstr.append(lon);
		urlstr.append(geourl3);
		// System.out.println(urlstr);

		String url2 = urlstr.toString();

		URL url = new URL(url2);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		InputStream stream = connection.getInputStream();
		// System.out.println(stream);
		// read the contents using an InputStreamReader

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String str;
		str = sb.toString();

		// System.out.println(str);
		JSONObject json2 = new JSONObject(str);
		JSONObject jsonT = json2.getJSONObject("main");
		result = jsonT.getDouble("temp");
		// System.out.println("Temp:" + result);
		return result;

	}

	/**
	 *
	 * @param args
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	@SuppressWarnings("empty-statement")
	public static void main(String[] args) throws JSONException, MalformedURLException, IOException {

		OpenWeather ow;
		ow = new OpenWeather();
		double lon = 12.89;
		double lat = 53.09;
		ow.setCoord(lon, lat);
		System.out.println("Windgeschwindigkeit: " + ow.getWind());
		// System.out.println("Temperatur: " + ow.getTemp());

	}

	public double getCloud() throws MalformedURLException, IOException {
		double result2 = 0;

		String geourl1 = ("http://api.openweathermap.org/data/2.5/weather?lat=");
		String georul2 = ("&lon=");
		// String geourl3 =
		// ("&appid=aaa67fe700a77d94eb3d115299f40e93&lang=de&units=Metric&temperature.unit=Celsius,");
		String geourl3 = ("&appid=aaa67fe700a77d94eb3d115299f40e93&lang=de&units=Metric");

		StringBuilder urlstr = new StringBuilder(geourl1);
		urlstr.append(lat);
		urlstr.append(georul2);
		urlstr.append(lon);
		urlstr.append(geourl3);
		// System.out.println(urlstr);

		String url2 = urlstr.toString();

		URL url = new URL(url2);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		InputStream stream = connection.getInputStream();
		// System.out.println(stream);
		// read the contents using an InputStreamReader

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		String str;
		str = result.toString();

		// log.info(str);
		JSONObject json2 = new JSONObject(str);
		/*
		 * 2017-09-24 10:15:01,024 INFO [OpenWeatherLogger]
		 * {"coord":{"lon":12.89,"lat":53.09},
		 * "weather":[{"id":300,"main":"Drizzle",
		 * "description":"Leichtes Nieseln","icon":"09d"},{"id":701,"main":
		 * "Mist","description":"Trüb","icon":"50d"}],"base":"stations","main":{
		 * "temp":14,"pressure":1021,"humidity":100,"temp_min":14,"temp_max":14}
		 * ,"visibility":2500, "wind":{"speed":2.6,"deg":60},
		 * "clouds":{"all":90},
		 * "dt":1506239400,"sys":{"type":1,"id":4892,"message":0.0038,"country":
		 * "DE","sunrise":1506229104,"sunset":1506272469},"id":2847612,"name":
		 * "Rheinsberg","cod":200}
		 */
		JSONObject json2Wind = json2.getJSONObject("clouds");
		result2 = json2Wind.getDouble("all");
		System.out.println("clouds:" + result2);
		return result2;
	}

	public JSONObject getWindCloud() throws MalformedURLException, IOException, JSONException {
		String geourl1 = ("http://api.openweathermap.org/data/2.5/weather?lat=");
		String georul2 = ("&lon=");
		String geourl3 = ("&appid=aaa67fe700a77d94eb3d115299f40e93&lang=de&units=Metric");

		StringBuilder urlstr = new StringBuilder(geourl1);
		urlstr.append(lat);
		urlstr.append(georul2);
		urlstr.append(lon);
		urlstr.append(geourl3);

		String url2 = urlstr.toString();

		URL url = new URL(url2);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		InputStream stream = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder receive = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			receive.append(line);
		}
		String str;
		str = receive.toString();

		JSONObject json2 = new JSONObject(str);
		JSONObject json2Wind = json2.getJSONObject("wind");
		double wind = json2Wind.getDouble("speed");

		JSONObject json2Cloud = json2.getJSONObject("clouds");
		double cloud = json2Cloud.getDouble("all");
		System.out.println("clouds:" + cloud);

		JSONObject result = new JSONObject();
		result.append("lat", lat);
		result.append("lon", lon);
		result.append("time", HoraTime.dateToStr(new Date()));
		result.append("wind", wind);
		result.append("cloud", cloud);

		return result;

	}

}
