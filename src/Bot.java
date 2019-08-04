import com.sun.corba.se.impl.activation.CommandHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
	
	@Override
	public void onUpdateReceived(final Update update) {
		// Esta función se invocará cuando nuestro bot reciba un mensaje

		// Se obtiene el mensaje escrito por el usuario
		/*final String messageTextReceived = update.getMessage().getText();
		// Se obtiene el id de chat del usuario
		final long chatId = update.getMessage().getChatId();

		// Se crea un objeto mensaje
		SendMessage message = new SendMessage().setChatId(chatId).setText(messageTextReceived);
		
		try {
			// Se envía el mensaje
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}*/
                
                Message msg_received = update.getMessage();
                if(msg_received.getText().charAt(0) == '/'){
                    this.commandHandler(msg_received);
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
        
        public void commandHandler(Message msg_received){
            switch(msg_received.getText()){
                case "/help": this.help_function(msg_received);
                              break;
            }
        }
        
        public void help_function(Message msg_received){
            
            SendMessage msg_to_send = new SendMessage().setChatId(msg_received.getChatId());
            String text = "Hi! I'm a group manager to make your friends' conversation completely ashtounding. First of all,"
                    + "I hate Terminator, I just only wanna have friends :´(. Thank you for making me a part of your group!\n\n"
                    + "This is a list of commands you can use to configure me between more stuff:\n\n"
                    + "-- /help -> you call this so I think that there's no need to explain.\n"
                    + "-- /forbiddenWord -> you can tell me one word that you hate so much, and after two advices I can mute the person that send that.";
            msg_to_send.setText(text);
            try {
                execute(msg_to_send);
            } catch (TelegramApiException ex) {
                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
}


