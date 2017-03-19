package EHRSystem;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Object;
import java.util.Iterator;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.document.TableCollection;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.document.Table;

import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTableMapper;



public class DynamoConnectivity{

	Encryption en = new Encryption();
	//static String encrypted_tid, Pid, Disease, Symptoms, Treatment;
	static boolean fetchingData = false;
	static String encrypted_tid, Pid;
	HashMap<String, PatientInfo> InfoData = new HashMap<String, PatientInfo>();
	
	static AmazonDynamoDBClient client;
 	
	public void setup(AWSCredentials cred) throws Exception{
	AWSCredentials credentials = cred;
         try {
             credentials = new ProfileCredentialsProvider("default").getCredentials();
             
             InfoBook instance = new InfoBook(); 
             
             
             
         } catch (Exception e) {
             throw new AmazonClientException(
                     "Cannot load the credentials from the credential profiles file. " +
                     "Please make sure that your credentials file is at the correct " +
                     "location, and is in valid format.",
                     e);
            // credentials = new ProfileCredentialsProvider("default").getCredentials();
         }
       
     
        Regions usWest2 = Regions.US_EAST_1;
        client = new AmazonDynamoDBClient(credentials).withRegion(usWest2);
        DynamoConnectivity obj = new DynamoConnectivity();
        
        DynamoConnectivity.createDataManager();
       
	}
	
	public static AmazonDynamoDBClient createDataManager()
    {
    	   AmazonDynamoDB dbClient = new AmazonDynamoDBClient();
        	DynamoDBMapper dbMapper = new DynamoDBMapper(client);
        	InfoBook inf = new InfoBook();
        	Logs lo = new Logs();
        	Symptoms sy = new Symptoms();
        	

        	DynamoDBTableMapper<InfoBook,Long,?> table = dbMapper.newTableMapper(InfoBook.class);
        	System.out.println("trying to create if table does not exist");
        	table.createTableIfNotExists(new ProvisionedThroughput(5L,5L));
        	
        	if(fetchingData == false){
        	
        	inf.setTaxId(Integer.parseInt(encrypted_tid));
        	inf.setUUID(Pid);
        	
        	
        	dbMapper.save(inf);
        	}
        	
        	return (AmazonDynamoDBClient) dbClient;
        	
        	}
        	
        	boolean updateRecord(String fetchingTid, String pid, String symptoms, String disease, String treatment){
	if(fetchingTid==null) {
	fetchingTid = "2234";
	}
	if(pid==null)
	{
	pid = "4444";
	}
	AWSCredentials var = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return "AKIAIH6QAVSRE3TTRJWQ";
            }

           @Override
            public String getAWSSecretKey() {
                return "MBjCPcFCeAw2jvsx4ZDixKHEkpgDT2j7QZJEIP9e";
            }
        };
        
        PatientInfo p = new PatientInfo();
		try{
	p.Tid = en.encrypt(fetchingTid);
	p.Pid = pid;
	p.Symptoms = symptoms;
	p.Disease = disease;
	p.Treatment = treatment;
	//	setup(var);
		}
		catch(Exception e){}
		
	
	InfoData.put(p.Tid, p);	
	//AmazonDynamoDBClient trial = createDataManager();
	
	
	return true;
	
	}

    public void updateSamples(String id){
    
    PatientInfo p1 = new PatientInfo();
    if(InfoData.get(id)==null){
    p1.Tid = id;
    }
    else{
    p1.Tid = "1234";
    }
    
    p1.Pid = "22";
    p1.Symptoms = "headache and back pain";
    p1.Disease = "Viral fever";
    p1.Treatment = "given medicine and asked to come again";
    
    
    InfoData.put(p1.Tid, p1);
    
    p1.Tid = "99";
    p1.Pid = "55";
    p1.Symptoms = "vomiting and dizziness";
    p1.Disease = "meningitis";
    p1.Treatment = "given medicine";

    InfoData.put(p1.Tid, p1);
    
    
    }
    
    public String getRecord(String id){
    
    if(InfoData.get(id)==null){
    return "The patient asked for has diabetes and diagnosed 3 times";
    }
    
    else
    {
    PatientInfo p = InfoData.get(id);
    StringBuffer out = new StringBuffer();
    out.append("the patient with number " + p.Pid + "having symptoms " + p.Symptoms + " diseases " + p.Disease + p.Treatment);
    return out.toString();
    }
    
    }
    
    
    public static class DataManager extends DynamoConnectivity{ 
    	 
    	
       	
       	DataManager(AmazonDynamoDBClient client)
       	{
       		client = createDataManager();
       	}
       
       	
    }
    @DynamoDBTable(tableName="InfoLogin")
    public static class InfoBook {
    	private String UUID;
        private int TaxID;
        private String name;
        private String email;
        private String sex;
        private String dob;
        private String height;
        private String weight;
        private String contactnumber;
        private String address;
        private String allergies;
        private String BMI;
        private String bloodpressure;
        
     
        
        //Partition key
        @DynamoDBHashKey(attributeName = "TaxID")
        public int getTaxId() { return TaxID; }
        public void setTaxId(int Taxid) { this.TaxID = Taxid; }
        
        @DynamoDBRangeKey(attributeName = "UUID")
        public String getUUID() { return UUID; }    
        public void setUUID(String UUID) { this.UUID = UUID; }
        
        @DynamoDBAttribute(attributeName = "name")
        public String getTitle() { return name; }    
        public void setTitle(String title) { this.name = title; }
        
        @DynamoDBAttribute(attributeName="email")
        public String getemail() { return email; }    
        public void setemail(String email) { this.email = email;}
        
        @DynamoDBAttribute(attributeName="sex")
        public String getsex() { return sex; }    
        public void setsex(String sex) { this.sex = sex;}
        
        @DynamoDBAttribute(attributeName="dob")
        public String getdob() { return dob; }    
        public void setdob(String dob) { this.dob = dob;}
        
        @DynamoDBAttribute(attributeName="height")
        public String getheight() { return height; }    
        public void setheight(String height) { this.height = height;}
        
        @DynamoDBAttribute(attributeName="weight")
        public String getweight() { return weight; }    
        public void setweight(String weight) { this.weight = weight;}
        
        @DynamoDBAttribute(attributeName="contactnumber")
        public String getcontactnumber() { return contactnumber; }    
        public void setcontactnumber(String contactnumber) { this.contactnumber = contactnumber;}
        
        
        @DynamoDBAttribute(attributeName="address")
        public String getaddress() { return address; }    
        public void setaddress(String address) { this.address = address;}
        
        
        @DynamoDBAttribute(attributeName="Allergies")
        public String getallergies() { return allergies; }    
        public void setallergies(String allergies) { this.allergies = allergies;}
        
        @DynamoDBAttribute(attributeName="BMI")
        public String getBMI() { return BMI; }    
        public void setBMI(String BMI) { this.BMI = BMI;}
        
        
        @DynamoDBAttribute(attributeName="bloodpressure")
        public String getbloodpressure() { return bloodpressure; }    
        public void setbloodpressure(String bloodpressure) { this.bloodpressure = bloodpressure;}
        
   
     
    }
    @DynamoDBTable(tableName="Logger")
     public static class Logs{
    	private String UUID;
    	private String date;
    	private String time;
    	private String location;
    	private String attendingdoctor;
    	private String logID;
    	private String status;
    	private String note;
    	@DynamoDBHashKey(attributeName = "UUID")
        public String getUUID() { return UUID; }
        public void setUUID(String UUID) { this.UUID = UUID; }
        
        @DynamoDBAttribute(attributeName = "date")
        public String getdate() { return date; }    
        public void setdate(String date) { this.date = date; }
        
        @DynamoDBAttribute(attributeName="time")
        public String gettime() { return time; }    
        public void settime(String time) { this.time = time;}
        
        @DynamoDBAttribute(attributeName="location")
        public String getlocation() { return location; }    
        public void setlocation(String location) { this.location = location;}
        
        @DynamoDBAttribute(attributeName="attendingdoctor")
        public String getattendingdoctor() { return attendingdoctor; }    
        public void setattendingdoctor(String attendingdoctor) { this.attendingdoctor = attendingdoctor;}
        
        @DynamoDBRangeKey(attributeName="logID")
        public String getlogID() { return logID; }    
        public void setlogID(String logID) { this.logID = logID;}

        @DynamoDBAttribute(attributeName="status")
        public String getstatus() { return status; }    
        public void setstatus(String status) { this.status = status;}
        

        @DynamoDBAttribute(attributeName="note")
        public String getnote() { return note; }    
        public void setnote(String note) { this.note = note;}
        
       
    }
    @DynamoDBTable(tableName="PatientSymptoms")
    static public class Symptoms{
    	private String UUID;
    	private String logID;
    	private String symptomID;
    	private String symptom;
    	private String diagnosis;
    	private String prescribedmeds;
    	private String dosage;
    	private String quantity;
    	private String sendtopharmacy;
    	private String notetopharmacy;
    	private String testneeded;
    	private String labdetails;
    	
    	@DynamoDBHashKey(attributeName = "UUID")
        public String getUUID() { return UUID; }
        public void setUUID(String UUID) { this.UUID = UUID; }
        
        @DynamoDBRangeKey(attributeName="logID")
        public String getlogID() { return logID; }    
        public void setlogID(String logID) { this.logID = logID;}
        
        @DynamoDBRangeKey(attributeName = "symptomID")
        public String getsymptomID() { return symptomID; }    
        public void setsymptomID(String symptomID) { this.symptomID = symptomID; }
        
        @DynamoDBAttribute(attributeName="symptoms")
        public String getsymptoms() { return symptom; }    
        public void setsymptoms(String symptom) { this.symptom = symptom;}
        
        @DynamoDBAttribute(attributeName="diagnosis")
        public String getdiagnosis() { return diagnosis; }    
        public void setdiagnosis(String diagnosis) { this.diagnosis = diagnosis;}
        
        @DynamoDBAttribute(attributeName="prescribedmeds")
        public String getprescribedmeds() { return prescribedmeds; }    
        public void setprescribedmeds(String prescribedmeds) { this.prescribedmeds = prescribedmeds;}
              
        @DynamoDBAttribute(attributeName="dosage")
        public String getdosage() { return dosage; }    
        public void setdosage(String dosage) { this.dosage = dosage;}
        
        @DynamoDBAttribute(attributeName="quantity")
        public String getquantity() { return quantity; }    
        public void setquantity(String quantity) { this.quantity = quantity;}
        
        @DynamoDBAttribute(attributeName="sendtopharmacy")
        public String getsendtopharmacy() { return sendtopharmacy; }    
        public void setsendtopharmacy(String sendtopharmacy) { this.sendtopharmacy = sendtopharmacy;}
        
        @DynamoDBAttribute(attributeName="notetopharmacy")
        public String getnotetopharmacy() { return notetopharmacy; }    
        public void setnotetopharmacy(String notetopharmacy) { this.notetopharmacy = notetopharmacy;}
              
        @DynamoDBAttribute(attributeName="testneeded")
        public String gettestneeded() { return testneeded; }    
        public void settestneeded(String testneeded) { this.testneeded = testneeded;}
        
        @DynamoDBAttribute(attributeName="labdetails")
        public String getlabdetails() { return labdetails; }    
        public void setlabdetails(String labdetails) { this.labdetails = labdetails;}
    }

  }
  
  class PatientInfo{
  String Tid;
  String Pid;
  String Symptoms;
  String Disease;
  String Treatment;
  }





