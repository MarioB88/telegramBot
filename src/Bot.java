import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.User;
import sun.util.logging.resources.logging;

public class Bot extends TelegramLongPollingBot {

    String forbidden_word = null;
    boolean flag1, flag2 = false;
    int contMsg = 0;
    int contBackup = 0;
    String the_game = "he perdido";
    ArrayList<Integer> participants = new ArrayList();
    ArrayList<Integer> advises = new ArrayList();
    ArrayList<Integer> members = new ArrayList();
    ArrayList<Integer> contGame = new ArrayList();
    
    String line;
    
    public Bot(){
        File f = null;
        FileReader fr = null;
        BufferedReader br = null;
        
        try {
            f = new File("C:\\Users\\Tito Berlih\\Desktop\\bot\\backup.txt");
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            forbidden_word = br.readLine();
            flag1 = Boolean.parseBoolean(br.readLine());
            flag2 = Boolean.parseBoolean(br.readLine());
            contMsg = Integer.parseInt(br.readLine());
            the_game = br.readLine();
            while(!(line = br.readLine()).equals("next")){
                participants.add(Integer.parseInt(line));
                advises.add(Integer.parseInt(br.readLine()));
            }
            while(!(line = br.readLine()).equals("end")){
                members.add(Integer.parseInt(line));
                contGame.add(Integer.parseInt(br.readLine()));
            }
        } catch (FileNotFoundException ex) {
            System.out.println("No se ha encontrado el fichero.\n Continúa la ejecución sin recuperar los datos.");
        } catch (IOException ex) {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
	@Override
	public void onUpdateReceived(final Update update) {
		
		Message msg_received = update.getMessage();
                String text_received = msg_received.getText();
                Long chatID = msg_received.getChatId();
                if(!members.contains(msg_received.getFrom().getId())){
                    members.add(msg_received.getFrom().getId());
                    contGame.add(0);
                }
                contMsg++;
                contBackup++;
                
		if (!msg_received.hasSticker() && text_received.charAt(0) == '/') {
                    try {
                        this.commandHandler(msg_received);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (TelegramApiException ex) {
                        Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                    }
		}
                
                if(flag1){
                    this.manage_forbWord(chatID, text_received);
                }
                else{
                    this.check_forbWord(chatID, msg_received);
                }
                
                if (flag2){
                    this.theGame(chatID, msg_received);
                }
                
                if(contBackup == 25){
                    this.backup();
                    contBackup = 0;
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
		return "Whoops";
	}

	public void commandHandler(Message msg_received) throws TelegramApiException, InterruptedException {
            Long chatID = msg_received.getChatId();
		switch (msg_received.getText()) {
		case "/help":
			this.help_function(chatID);
			break;
                case "/forbiddenword":
                        this.forbiddenWord(chatID);
                        break;
                case "/thegame":
                        if(!flag2){
                            flag2 = true;
                            this.send_message(chatID, "Let the war begin...");
                            this.send_sticker(chatID, "CAADAgADAwADr8ZRGug5n46ojYuhFgQ");
                            break;
                        }else{
                            flag2 = false;
                            String textGame = "What a coward! This is how was the competition going:\n\n";
                            for (int i = 0; i < members.size(); i++) {
                                GetChatMember getMember = new GetChatMember().setChatId(chatID).setUserId(members.get(i));
                                try {
                                    ChatMember chMember = execute(getMember);
                                    textGame = textGame + chMember.getUser().getFirstName() + " --> " + contGame.get(i) + " defeats\n";
                                } catch (TelegramApiException ex) {
                                    Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            this.send_message(chatID, textGame);
                            break;
                        }
                default: 
                    this.send_message(chatID, "That's not a command! Check your writing!");      
		}
	}

	public void help_function(Long id_chat) {
            String text = "Hi! I'm a group manager to make your friends' conversation completely ashtounding. First of all, "
				+ "I hate Terminator, I just only wanna have friends :´(. Thank you for making me a part of your group!\n\n"
				+ "This is a list of commands you can use to configure me between more stuff:\n\n"
				+ "-- /help -> you call this so I think that there's no need to explain.\n"
				+ "-- /forbiddenword -> you can tell me one word that you hate so much, and after two advices I can mute the person that send that.\n"
				+ "-- /thegame -> write the name and the times that people have lost.";
            this.send_message(id_chat, text);
	}
	
	public void forbiddenWord(Long id_chat) throws InterruptedException, TelegramApiException{
                flag1 = true;
		SendMessage msgFirst = new SendMessage().setChatId(id_chat);
		msgFirst.setText("What do you want to do?\n- Add forbidden word, write the number 1 and your word after a space (1 Silly)\n- Delete forbidden word, write the number 2\n- Check your forbidden word, write the number 3\n");
                execute(msgFirst);
                //Aqui va lo del sticker
		

	}
        
        public void send_message(long ID, String text){
            try {
                SendMessage msg_to_send = new SendMessage();
                msg_to_send.setChatId(ID);
                msg_to_send.setText(text);
                execute(msg_to_send);
            } catch (TelegramApiException ex) {
                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void send_sticker(long ID, String stID){
            try {
                SendSticker sticker = new SendSticker().setChatId(ID).setSticker(stID);
                execute(sticker);
            } catch (TelegramApiException ex) {
                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void manage_forbWord(long chatID, String text_received){
            switch(text_received.charAt(0)){
                        case '1':
                            forbidden_word = text_received.substring(2);
                            this.send_message(chatID, "Done!");
                            this.send_sticker(chatID, "CAADAgADAwADmL-ADUV9BIgmMsLjFgQ");
                            flag1 = false;
                            break;
                        case '2':
                            forbidden_word = null;
                            this.send_message(chatID, "Done!");
                            this.send_sticker(chatID, "CAADAgADAwADmL-ADUV9BIgmMsLjFgQ");
                            flag1 = false;
                            break;
                        case '3':
                            SendMessage checkWord = new SendMessage();
                            checkWord.setChatId(chatID);
                            if(forbidden_word != null){
                                this.send_message(chatID, forbidden_word);
                            }else{
                                this.send_message(chatID, "There isn't any forbidden word.");
                                this.send_sticker(chatID, "CAADAgAD-QADVp29CpVlbqsqKxs2FgQ");
                            }
                            flag1 = false;
                            break;
                    }
        }
        
        public void check_forbWord(long chatID, Message msg_received){
            String text_received = msg_received.getText();
            if(forbidden_word != null && (text_received.contains(forbidden_word) || text_received.contains(forbidden_word.toLowerCase()) || text_received.contains(forbidden_word.toUpperCase()))){
                        if (participants.contains(msg_received.getFrom().getId())){
                            int ind = participants.indexOf(msg_received.getFrom().getId());
                            if (advises.get(ind) == 1){
                                this.send_message(chatID, "Ah shit, here we go again");
                                this.send_sticker(chatID, "CAADAgADCQADmL-ADRK8edwuVum2FgQ");
                                advises.set(ind, 0);
                                KickChatMember kick = new KickChatMember(chatID, msg_received.getFrom().getId());
                                try {
                                    execute(kick);
                                } catch (TelegramApiException ex) {
                                    Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }else {
                                this.send_message(chatID, "First advise, don't write again " + forbidden_word);
                                this.send_sticker(chatID, "CAADAgADAgEAAladvQpO4myBy0Dk_xYE");
                                advises.set(ind, 1);
                            }
                        }else{
                            participants.add(msg_received.getFrom().getId());
                            advises.add(1);
                            this.send_message(chatID, "First advise, don´t write again " + forbidden_word);
                            this.send_sticker(chatID, "CAADAgADAgEAAladvQpO4myBy0Dk_xYE");
                        }
                    }
            
        }
        
        public void theGame(long chatID, Message msg_received){
            String text_received = msg_received.getText();
            
            if(!msg_received.hasSticker() && text_received.toLowerCase().contains(the_game)){
                        int pos = members.indexOf(msg_received.getFrom().getId());
                        String textGame = null;
                        contGame.set(pos, contGame.get(pos)+1);
                        for (int i = 0; i < members.size(); i++) {
                            GetChatMember getMember = new GetChatMember().setChatId(chatID).setUserId(members.get(i));
                            try {
                                ChatMember chMember = execute(getMember);
                                textGame = msg_received.getFrom().getFirstName() + " you suck! How is the competition going?\n\n";
                                textGame = textGame + chMember.getUser().getFirstName() + " --> " + contGame.get(i) + " defeats\n";
                            } catch (TelegramApiException ex) {
                                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        this.send_message(chatID, textGame);
                        this.send_sticker(chatID, "CAADAgADAwADr8ZRGug5n46ojYuhFgQ");
                    }
        }
        
        public void backup(){
            FileWriter f = null;
            PrintWriter pw = null;
            try {
                f = new FileWriter("C:\\Users\\Tito Berlih\\Desktop\\bot\\backup.txt");
                pw = new PrintWriter(f);
                
                pw.println(forbidden_word);
                pw.println(flag1);
                pw.println(flag2);
                pw.println(contMsg);
                pw.println(the_game);
                for (int i = 0; i < participants.size(); i++) {
                    pw.println(participants.get(i));
                    pw.println(advises.get(i));
                }
                pw.println("next");
                for (int i = 0; i < members.size(); i++) {
                    pw.println(members.get(i));
                    pw.println(contGame.get(i));
                }
                pw.println("end");
                
            } catch (IOException ex) {
                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally{
                if(f != null){
                    try {
                        f.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
}


