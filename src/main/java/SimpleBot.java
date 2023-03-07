import java.util.ArrayList;
import java.util.List;
import me.bush.translator.Language;
import me.bush.translator.Translator;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SimpleBot extends TelegramLongPollingBot {

  Translator translator = new Translator();

  Language languageDetector(String language) {
    Language result;
    result = switch (language) {
      case "ru" -> Language.RUSSIAN;
      case "en" -> Language.ENGLISH;
      case "de" -> Language.GERMAN;
      case "fr" -> Language.FRENCH;
      case "es" -> Language.SPANISH;
      case "tr" -> Language.TURKISH;
      case "pl" -> Language.POLISH;
      default -> null;
    };
    return result;
  }

  @Override
  public void onUpdateReceived(Update update) {



    if (update.hasMessage() && update.getMessage().hasText()) {
      if (update.getMessage().getText().equals("/start")) {

        SendMessage messageOutput = new SendMessage();
        messageOutput.setChatId(update.getMessage().getChatId().toString());
        messageOutput.setText("To translate text, write your request in the form:\n"+ "<language1>-><language2>: <Text>.\n"
            + "Available languages: en(English), ru(Russian), de(Deutch), fr(French), es(Espanish), tr(Turkish), pl(Polish).");

        try {
          execute(messageOutput);
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
      } else {
        SendMessage messageOutput = new SendMessage();
        messageOutput.setChatId(update.getMessage().getChatId().toString());

        Language lang_to = languageDetector(update.getMessage().getText().substring(0, 2));
        Language lang_from = languageDetector(update.getMessage().getText().substring(4, 6));
        String text = update.getMessage().getText().substring(7, update.getMessage().getText().length());
        if(lang_to != null && lang_from != null && lang_to!=lang_from ) {
          messageOutput.setText( translator.translateBlocking(text, lang_from, lang_to).getTranslatedText());
        }else{
          messageOutput.setText("Wrong request format. Try again.");
        }
        try {
          execute(messageOutput);
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
      }

    }
  }

  @Override
  public String getBotUsername() {
    return "botName";
  }

  @Override
  public String getBotToken() {
    return "TelegramAPIToken";
  }
}
