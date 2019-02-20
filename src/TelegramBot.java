
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



public class TelegramBot {

	public static void main(String[] args) {
		
		ApiContextInitializer.init();
		 
		// Se crea un nuevo Bot API
		final TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
 
		try {
			// Se registra el bot
			telegramBotsApi.registerBot(new Bot());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

}
}
