import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
	
	@Override
	public void onUpdateReceived(final Update update) {
		// Esta función se invocará cuando nuestro bot reciba un mensaje

		// Se obtiene el mensaje escrito por el usuario
		final String messageTextReceived = update.getMessage().getText();
		// Se obtiene el id de chat del usuario
		final long chatId = update.getMessage().getChatId();

		// Se crea un objeto mensaje
		SendMessage message = new SendMessage().setChatId(chatId).setText(messageTextReceived);
		
		try {
			// Se envía el mensaje
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBotUsername() {
		// Se devuelve el nombre que dimos al bot al crearlo con el BotFather
		return "JustRodeBot";
	}

	@Override
	public String getBotToken() {
		// Se devuelve el token que nos generó el BotFather de nuestro bot
		return "714010306:AAGiZNEKesKuGn3kNhOShrPtDdN1K6IKt5M";
	}
}


