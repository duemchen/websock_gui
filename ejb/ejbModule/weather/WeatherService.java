package weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import database.Spiegel;
import database.SpiegelController;

/**
 * @author duemchen TODO für jeden spiegel eigene daten abrufen und - in
 *         datenbank eintragen zum Spiegel - der controller kann dann damit in
 *         Sturm steuern - die Webgui kann aktuelles wetter anzeigen
 *
 */

public class WeatherService {

	private volatile boolean isCancelled = false;
	private static SimpleDateFormat sdf = new SimpleDateFormat("mm");
	private final int XMINUTEN = 15;
	private SpiegelController spiegelController;

	public WeatherService(SpiegelController spiegelController) {
		this.spiegelController = spiegelController;
	}

	public void startService() { // infinit loop:
		int last = -1;
		while (!isCancelled) {

			try {
				Thread.sleep(200);
				// minuten
				int now = Integer.parseInt(sdf.format(new Date()));
				// XMINUTEN);
				now = now / XMINUTEN;

				if (now != last) {
					last = now;
					System.out.println("Weather ");
					last = now;
					OpenWeather ow = new OpenWeather();
					double lon = 12.89;
					double lat = 53.09;
					ow.setCoord(lon, lat);
					double wind = ow.getWind();
					double cloud = ow.getCloud();
					List<Spiegel> sps = spiegelController.getSpiegels();
					for (Spiegel s : sps) {
						s.setWind(wind);
						s.setWolke(cloud);
						spiegelController.update(s);
					}
					System.out.println("Wind: " + wind + ", Cloud: " + cloud);
					// System.out.println(ow.getWindCloud());

				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	public void stopService() {
		isCancelled = true;
	}

}
