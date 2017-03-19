# Electronic Health Records Alexa

We are focusing on the problem of managing Electronic Health Records, also known as EHRs. EHRs are still not available in many developing countries and the busy environment in the medical field makes it difficult for healthcare professionals to keep it updated. Now in the age of Alexa, these professionals now just have to talk to Alexa and tell her all the records they wanna update or fetch and Alexa will do the rest. Yes, they just have to tell the information and Alexa will update the EHRs.

## Setup
To run this skill you need to do two things. The first is to deploy the example code in lambda, and the second is to configure the Alexa skill to use Lambda.

### AWS Lambda Setup
1. Go to the AWS Console and click on the Lambda link. Note: ensure you are in us-east or you wont be able to use Alexa with Lambda.
2. Click on the Create a Lambda Function or Get Started Now button.
3. Skip the blueprint
4. Name the Lambda Function "EHR-system".
5. Select the runtime as Java 8
6. Go to the the root directory containing pom.xml, and run 'mvn assembly:assembly -DdescriptorId=jar-with-dependencies package'. This will generate a zip file named "alexa-skills-kit-samples-1.0-jar-with-dependencies.jar" in the target directory.
7. Select Code entry type as "Upload a .ZIP file" and then upload the "alexa-skills-kit-samples-1.0-jar-with-dependencies.jar" file from the build directory to Lambda
8. Set the Handler as EHRSystem.EHRsystemSpeechletRequestStreamHandler (this refers to the Lambda RequestStreamHandler file in the zip).
9. Create a basic execution role and click create.
10. Leave the Advanced settings as the defaults.
11. Click "Next" and review the settings then click "Create Function"
12. Click the "Event Sources" tab and select "Add event source"
13. Set the Event Source type as Alexa Skills kit and Enable it now. Click Submit.
14. Copy the ARN from the top right to be used later in the Alexa Skill Setup.

### Alexa Skill Setup
1. Go to the [Alexa Console](https://developer.amazon.com/edw/home.html) and click Add a New Skill.
2. Set "EHR System" as the skill name and "Electronic Health Record System" as the invocation name, this is what is used to activate the skill. For example you would say: "Alexa, start the Electronic Health Record System."
3. Select the Lambda ARN for the skill Endpoint and paste the ARN copied from above. Click Next.
4. Copy the Intent Schema from the included IntentSchema.json.
5. Copy the Sample Utterances from the included SampleUtterances.txt. Click Next.
6. Go back to the skill Information tab and copy the appId. Paste the appId into the EHRSystemSpeechletRequestStreamHandler.java file for the variable supportedApplicationIds,
   then update the lambda source zip file with this change and upload to lambda again, this step makes sure the lambda function only serves request from authorized source.
7. You are now able to start using the skill! You should be able to go to the [Echo webpage](http://echo.amazon.com/#skills) and see the skill enabled.
8. In order to test it, try to say some of the Sample Utterances from the Examples section below.
9. The skill is now saved and once you are finished testing you can continue to use it.

### Examples
#### One-shot model:
    User: "Alexa, start the Electronic Health Record System."
    Alexa: "Welcome to the Electronic Health Record System!"

## Alexa Skills Kit Documentation
The documentation for the Alexa Skills Kit used in this project is available on the [Amazon Apps and Services Developer Portal](https://developer.amazon.com/appsandservices/solutions/alexa/alexa-skills-kit/).

## Resources
Here are a few direct links to Amazon's documentation that helped us in our project:

- [Using the Alexa Skills Kit Samples](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/using-the-alexa-skills-kit-samples)
- [Getting Started](https://developer.amazon.com/appsandservices/solutions/alexa/alexa-skills-kit/getting-started-guide)
- [Invocation Name Guidelines](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/choosing-the-invocation-name-for-an-alexa-skill)
- [Developing an Alexa Skill as an AWS Lambda Function](https://developer.amazon.com/appsandservices/solutions/alexa/alexa-skills-kit/docs/developing-an-alexa-skill-as-a-lambda-function)
