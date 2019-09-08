import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import java.util.ArrayList;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.pengrad.telegrambot.response.BaseResponse;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;

public class Bot extends TelegramLongPollingBot {

    String forbidden_word = null;
    boolean flag1 = false;
    ArrayList participants = new ArrayList();
    ArrayList<Integer> advises = new ArrayList();
    int contMsg = 0;
                        
    
	@Override
	public void onUpdateReceived(final Update update) {
		// Esta función se invocará cuando nuestro bot reciba un mensaje

		// Se obtiene el mensaje escrito por el usuario
		/*
		 * final String messageTextReceived = update.getMessage().getText(); // Se
		 * obtiene el id de chat del usuario final long chatId =
		 * update.getMessage().getChatId();
		 * 
		 * // Se crea un objeto mensaje SendMessage message = new
		 * SendMessage().setChatId(chatId).setText(messageTextReceived);
		 * 
		 * try { // Se envía el mensaje execute(message); } catch (TelegramApiException
		 * e) { e.printStackTrace(); }
		 */

		Message msg_received = update.getMessage();
                String text_received = msg_received.getText();
                Long chatID = msg_received.getChatId();
		if (text_received.charAt(0) == '/') {
                    try {
                        this.commandHandler(msg_received);
                    } catch (TelegramApiException ex) {
                        Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                    }
		}
                if(flag1){
                    switch(text_received.charAt(0)){
                        case '1':
                            forbidden_word = text_received.substring(3);
                            break;
                        case '2':
                            forbidden_word = null;
                            break;
                        case '3':
                            SendMessage checkWord = new SendMessage();
                            checkWord.setChatId(chatID);
                            checkWord.setText(forbidden_word);
                            break;
                        default:
                            SendMessage errorMsg = new SendMessage();
                            errorMsg.setChatId(chatID);
                            errorMsg.setText("Something went wrong, try to execute again the command");
                    {
                        try {
                            execute(errorMsg);
                        } catch (TelegramApiException ex) {
                            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                            break;
                    }
                    flag1 = false;
                }
                else{
                    if(text_received.contains(forbidden_word)){
                        if (participants.contains(msg_received.getFrom())){
                            int ind = participants.indexOf(msg_received.getFrom());
                            if (advises.get(ind) == 1){
                                SendMessage kickMsg = new SendMessage();
                                kickMsg.setChatId(chatID);
                                kickMsg.setText("Ah shit, here we go again.");
                                try {
                                    execute(kickMsg);
                                } catch (TelegramApiException ex) {
                                    Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                advises.set(ind, 0);
                                KickChatMember kick = new KickChatMember(chatID, msg_received.getFrom().getId());
                                try {
                                    execute(kick);
                                } catch (TelegramApiException ex) {
                                    Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }else {
                                SendMessage advMsg = new SendMessage();
                                advMsg.setChatId(chatID);
                                advMsg.setText("First advise, don´t write again " + forbidden_word);
                                try {
                                    execute(advMsg);
                                } catch (TelegramApiException ex) {
                                    Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                advises.set(ind, 1);
                            }
                        }else{
                            participants.add(msg_received.getFrom());
                            advises.add(1);
                            SendMessage advMsg = new SendMessage();
                                advMsg.setChatId(chatID);
                                advMsg.setText("First advise, don´t write again " + forbidden_word);
                                try {
                                    execute(advMsg);
                                } catch (TelegramApiException ex) {
                                    Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
                        
                    }
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
		return "myToken";
	}

	public void commandHandler(Message msg_received) throws TelegramApiException {
            Long chatID = msg_received.getChatId();
		switch (msg_received.getText()) {
		case "/help":
			this.help_function(chatID);
			break;
                default: 
                    SendMessage errorMsg = new SendMessage();
                    errorMsg.setChatId(chatID);
                    errorMsg.setText("That's not a command! Check your writing!");
                    execute(errorMsg);
                        
		}
	}

	public void help_function(Long id_chat) {

		SendMessage msg_to_send = new SendMessage().setChatId(id_chat);
		String text = "Hi! I'm a group manager to make your friends' conversation completely ashtounding. First of all,"
				+ "I hate Terminator, I just only wanna have friends :´(. Thank you for making me a part of your group!\n\n"
				+ "This is a list of commands you can use to configure me between more stuff:\n\n"
				+ "-- /help -> you call this so I think that there's no need to explain.\n"
				+ "-- /forbiddenWord -> you can tell me one word that you hate so much, and after two advices I can mute the person that send that.\n"
				+ "-- /theGame -> write the name and the times that that person lose in a table.";
		msg_to_send.setText(text);
		try {
			execute(msg_to_send);
		} catch (TelegramApiException ex) {
			Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public String forbiddenWord(Long id_chat) throws InterruptedException{
		flag1 = true;
                String strResult = null;
		SendMessage msgFirst = new SendMessage().setChatId(id_chat);
		msgFirst.setText("What do you want to do?\n- Add forbidden word, write the number 1 and your word after a space (1 Silly)\n- Delete forbidden word, write the number 2 and your word after a space\n- Check your forbidden word, write the number 3\n");
		//Aqui va lo del sticker
                
                this.wait();
                
                return strResult;

	}

	}


