# Naive Bayes Email Classifier

This application is built with Clojure and leverages the Naive Bayes classification algorithm to determine whether an email is spam (unsolicited) or ham (legitimate). It provides RESTful API endpoints for the classification of emails, as well as retrieval and storage of email data, interfacing with a MySQL database.

## How It Works

### Naive Bayes Classifier

The Naive Bayes classifier is a probabilistic machine learning model used for classification tasks. It is based on Bayes' theorem with the naive assumption of conditional independence between every pair of features given the value of the class variable. In the context of spam detection, this classifier considers each word in an email as an independent feature and calculates the probability of an email being spam or ham based on the frequency of these words.

### Training the Model

The classifier is trained on a dataset of pre-labeled emails, where each email is already marked as spam or ham. This process involves two key steps:

1. **Preprocessing**: Each email is preprocessed to convert its content into a format suitable for the classifier. This typically includes converting text to lowercase, removing punctuation, tokenizing the text into words (also known as the bag of words model), and filtering out common stop words.

2. **Learning**: Using the preprocessed emails, the classifier learns by calculating the prior probability of each class (spam or ham) and the likelihood of each word given the class. This is done by counting the occurrences of each word in emails of each class.

### Predicting New Emails

When a new email arrives, the classifier preprocesses it in the same way as during training and then applies the learned probabilities to predict whether the new email is spam or ham.


## Features

- REST API to classify emails
- Endpoints to list and retrieve emails
- Functionality to insert email and user data into the system

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

- [Clojure](https://clojure.org/guides/getting_started)
- [Leiningen](https://leiningen.org/) for project management
- [Docker](https://www.docker.com/products/docker-desktop) for running the MySQL database

### Installation

Clone the repository to your local machine.

```sh
git clone https://github.com/UrosVesic/naive-bayes-classifier.git
cd naive-bayes-classifier
```

### Set Up the Database with Docker Before running the application, start the MySQL database using Docker:
```sh
docker compose up -d
```

### Run the application using Leiningen.
```sh
lein run
```
The server will start, and you can access the API at http://localhost:3000

## API Endpoints

The application exposes a RESTful API with the following endpoints:

### Email Classification

- `POST /classify` 
  - **Description:** Accepts an email text and classifies it as spam or ham.
  - **Body:** `{ "email": "Email text to classify" }`
  - **Response:** `{ "prediction": "spam" }` or `{ "prediction": "ham" }`

### Email Retrieval

- `GET /email/{receiver}`
  - **Description:** Retrieves all emails for the specified receiver.
  - **Parameters:** `receiver` - The identifier for the email receiver.
  - **Response:** A JSON array of email objects.

### Email Insertion

- `POST /email`
  - **Description:** Accepts email details and inserts the email into the database.
  - **Body:** `{ "sender": "Sender's email", "receiver": "Receiver's email", "subject": "Email subject", "content": "Email content" }`
  - **Response:** `{
        "receiver": "uros",
        "sender": "pera",
        "subject": "inheritance",
        "content": "Nigerian prince left you big inheritance, click here to claim!",
        "spam": true
    }`

## Example Usage

Here are some example usages of the API:

**Classify an Email:**

```sh
curl -X POST http://localhost:3000/classify \
-H "Content-Type: application/json" \
-d '{"email":"Sample email content to classify."}'
```

**Insert User:**

```sh
curl -X POST http://localhost:3000/user \
-H "Content-Type: application/json" \
-d '{"email":"johndoe@example.com"}'
```

**Retrieve Emails for a Receiver:**

```sh
curl http://localhost:3000/email/1
```

**Insert an email**

```sh
curl -X POST http://localhost:3000/email \
-H "Content-Type: application/json" \
-d '{"sender":"janedoe@example.com", "receiver":"johndoe@example.com", "subject":"Meeting Schedule", "content":"Lets schedule the meeting for next Thursday."}'
```

## Testing
### Running Tests
Tests in this project are designed to verify the functionality of the Naive Bayes Email Classifier, including its API endpoints, classification logic, and interaction with the database. To run the tests, use the following command:
```sh
lein test
```

Testcontainers will automatically start a MySQL container for the tests, and it will be stopped and removed after the tests complete. This provides a consistent and isolated environment for testing database-related functionality.



