
package EHRSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.*;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import java.util.Map;
import java.util.HashMap;


public class EHRSystemSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(EHRSystemSpeechlet.class);
	HashMap<String, String> data = new HashMap<String, String>();
	DynamoConnectivity db = new DynamoConnectivity();
	String x = "10";
	String y = "17";
	boolean fetchingData = false;
	String fetchingTid;
	
	
	
    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("StartEnterProcess".equals(intentName)) {
            return getStartEnterProcessResponse();
            }
            else if("EnterTaxId".equals(intentName)){
            return getEnterTaxIdResponse(intent);
        } else if ("EnterRecord".equals(intentName)){
            return getEnterRecordResponse(intent); 
            }
            else if ("StartFetchProcess".equals(intentName)){
            return getStartFetchProcessResponse();
            }
            else if ("FetchRecord".equals(intentName) && fetchingData){
            return getFetchRecordResponse(intent);
            }
            else if("Emergency".equals(intentName)){
            return getEmergencyResponse();
            }
            else if("AMAZON.YesIntent".equals(intentName)){
            return getYesResponse();
            }
            else if("AMAZON.StopIntent".equals(intentName)){
            return getStopResponse();
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        
    }

   
    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Welcome to the Electronic Health Record System. What would you like to do, enter patient data or fetch records";

	data.put(x,y);
	
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    
    private SpeechletResponse getStartEnterProcessResponse() {
        String speechText = "Okay, give me the Tax number";
        fetchingData = false;

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

	// Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
        
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
    
    private SpeechletResponse getEnterTaxIdResponse(Intent intent){
    Map<String, Slot> slots = intent.getSlots();
    
    String speechText;
    
    Slot tid = slots.get("PID");
    
    if(tid!=null){
    fetchingTid = tid.getValue();
    speechText = "Tax number entered. you can now tell me the patient details";
    }
    else{
    speechText = "I didn't understood what you said. Please say again";
    }
    
    // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

	// Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
        
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
    
    private SpeechletResponse getEnterRecordResponse(Intent intent){
    Map<String, Slot> slots = intent.getSlots();
    
    String speechText = "check response";
    
    Slot pid = slots.get("PID");
    Slot disease = slots.get("Disease");
    Slot symptoms = slots.get("Symptoms");
    Slot treatment = slots.get("Treatment");
    
    String p_id, p_symptoms, p_disease, p_treatment;
    //disease!=null && symptoms!=null && treatment!=null
    if(pid!=null ){
    data.put("10", disease.getValue());
    p_id = pid.getValue();
    p_symptoms = symptoms.getValue();
    p_disease = disease.getValue();
    p_treatment = treatment.getValue();
    
    if(db.updateRecord(fetchingTid, p_id, p_symptoms, p_disease, p_treatment)){
    speechText = "Data Updated: Patient with number " + p_id + " having symptoms " + p_symptoms + " and disease " + p_disease + p_treatment + " Would you like to enter more data or fetch any records";
    }
    else{
    speechText = "Data update unsuccessful. Please try again";
    }
    }
    else
    {
    speechText = "I am not sure what you said " + " Would you like to try again ";
    }
    
    SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

	Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
        
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    
    }
    
     private SpeechletResponse getStartFetchProcessResponse() {
        String speechText = "Yes, go ahead and tell me Tax number of whose data you want";
	//String speechText = "Patient id 10 has disease number" + data.get("10");
        // Create the Simple card content.
        fetchingData = true;
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);
        
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

	
	private SpeechletResponse getFetchRecordResponse(Intent intent){
	String speechText;
	String resp;
	Map<String, Slot> slots = intent.getSlots();
	
	Slot pid = slots.get("PID");
	
	if(pid!=null){
	 //data.get(pid.getValue());
	 
	 db.updateSamples(pid.getValue());
	resp = db.getRecord(pid.getValue());
	
	speechText = resp + "Would you like to know more";
	}
	else{
	speechText = "I am not sure what you said Would you like to try again";
	}
	
	SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);
        
        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
	
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
	
	}
	
	private SpeechletResponse getYesResponse() {
        String speechText = "Okay, go ahead and tell me what do you want to do";
	
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

	// Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
        
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
     
     
     private SpeechletResponse getEmergencyResponse() {
        String speechText = "This Patient has sugar and diabetes and got diagnosed by a doctor 9 timmes";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

	
        
        return SpeechletResponse.newTellResponse(speech, card);
    }
    
    private SpeechletResponse getStopResponse() {
        String speechText = "Okay GoodBye";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

	
        
        return SpeechletResponse.newTellResponse(speech, card);
    }
    
    private SpeechletResponse getHelpResponse() {
        String speechText = "You can say hello to me!";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
}
