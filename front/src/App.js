import React, {Component} from 'react';
import './App.css';
import Calendar from "./calendar/Calendar";
import {Login} from "./login";
import {initializeClasses, retrieveClasses, retrieveEnrollmentDeclaration} from "./requests";

class App extends Component {

  test = {
    "message": "Processing",
    "content": [
      {
        "name": "Class1 - (CODE1)",
        "timeslots": [
          {
            "classIdentifier": "U",
            "availableSlots": 10,
            "professors": [
              "PROFESSOR1"
            ],
            "scheduledTimes": [
              {
                "day": "Segunda",
                "shortDay": "seg",
                "startTime": "08:30",
                "endTime": "10:10",
                "location": "Classroom 123 - Building 456",
                "locationMap": "http://link.com/to/location1"
              },
              {
                "day": "Quarta",
                "shortDay": "qua",
                "startTime": "08:30",
                "endTime": "10:10",
                "location": "Classroom 123 - Building 456",
                "locationMap": "http://link.com/to/location1"
              }
            ]
          }
        ],
        "credits": 4,
        "classPlan": "http://link.com/to/classplan1.pdf"
      },
      {
        "name": "Class2 - (CODE2)",
        "timeslots": null,
        "credits": null,
        "classPlan": null
      }
    ]
  };
  calendarz = [
    [
      {
        "name": "Class1 - (CODE1)",
        "timeslots": [
          {
            "classIdentifier": "T",
            "availableSlots": 10,
            "professors": [
              "PROFESSOR1"
            ],
            "scheduledTimes": [
              {
                "day": "Segunda",
                "shortDay": "seg",
                "startTime": "08:30",
                "endTime": "10:10",
                "location": "Classroom 123 - Building 456",
                "locationMap": "http://link.com/to/location1"
              },
              {
                "day": "Quarta",
                "shortDay": "qua",
                "startTime": "08:30",
                "endTime": "10:10",
                "location": "Classroom 123 - Building 456",
                "locationMap": "http://link.com/to/location1"
              }
            ]
          },
          {
            "classIdentifier": "U",
            "availableSlots": 1,
            "professors": [
              "PROFESSOR1"
            ],
            "scheduledTimes": [
              {
                "day": "Terca",
                "shortDay": "ter",
                "startTime": "08:30",
                "endTime": "10:10",
                "location": "Classroom 123 - Building 456",
                "locationMap": "http://link.com/to/location1"
              },
              {
                "day": "Quinta",
                "shortDay": "qui",
                "startTime": "08:30",
                "endTime": "10:10",
                "location": "Classroom 123 - Building 456",
                "locationMap": "http://link.com/to/location1"
              }
            ]
          }
        ],
        "credits": 4,
        "classPlan": "http://link.com/to/location1"
      },
      {
        "name": "Class2 - (CODE1)",
        "timeslots": [
          {
            "classIdentifier": "U",
            "availableSlots": 10,
            "professors": [
              "PROFESSOR1"
            ],
            "scheduledTimes": [
              {
                "day": "Segunda",
                "shortDay": "seg",
                "startTime": "08:30",
                "endTime": "10:10",
                "location": "Classroom 123 - Building 456",
                "locationMap": "http://link.com/to/location1"
              },
              {
                "day": "Quarta",
                "shortDay": "qua",
                "startTime": "08:30",
                "endTime": "10:10",
                "location": "Classroom 123 - Building 456",
                "locationMap": "http://link.com/to/location1"
              }
            ]
          }
        ],
        "credits": 4,
        "classPlan": "http://link.com/to/location3"
      }
    ]
  ]


  firstFetch = true // fiz soh para nao spammar, tem que ver maneira melhor de fazer
  firstInitializeRequest = true
  firstEnrollmentRequest = true

  constructor(props) {
    super(props);
    this.state = {
      isLoggedIn: false,
      requestCode: false,
      enrollmentDeclarationLink: '',
      calendars: []
    }
  }

  initializeRequestCode() {
    const code = async() => {
      let response = await initializeClasses()
      
      switch (response.statusCode) {
        case 200:
          this.setState({...this.state, requestCode: response.content.content})
          break
        case 400: // ou cookie nao eh string ou nao tem nenhuma aula
        case 401: // ou sem cookie ou cookie outdated
        case 501: // erro no parse da resposta da ufrgs
        case 502: // erro de network com a ufrgs
        case 500: // erro generico, tratar junto com default
        default: // nao deve ser possivel
      }
    }
    if (this.firstInitializeRequest) {
      this.firstInitializeRequest = false
      code()
    }
  }

  retrieveRequestCode() {
    const classes = async() => {
      let respose = await retrieveClasses(this.state.requestCode)
      let sleep = () => new Promise(resolve => setTimeout(resolve, 1000))

      while (respose.statusCode === 206) { // resultado parcial, ficar dando query ateh completo
        if (this.state.calendars !== respose.content.content) {
          this.setState({...this.state, calendars: respose.content.content})
        }
        console.log("Resultado parcial")
        console.log(respose.content.content) // probs setState?
        await sleep() // 1 query/sec
        respose = await retrieveClasses(this.state.requestCode)
      }

      switch (respose.statusCode) {
        case 200:
          this.setState({...this.state, calendars: respose.content.content})
          console.log("Resultado completo")
          console.log(respose.content.content) // probs setState
          break
        case 400: // this.state.requestCode nao reconhecido
        case 500: // erro generico, tratar junto com default
        default: // nao deve ser possivel
      }
    }
    if (!!this.state.requestCode && this.firstFetch) {
      this.firstFetch = false
      classes()
    }
  }

  getEnrollmentDeclaration() {
    const link = async() => {
      let response = await retrieveEnrollmentDeclaration()

      switch(response.statusCode) {
        case 200:
          console.log(response.content.content)
          this.setState({...this.state, enrollmentDeclarationLink: response.content.content})
          break
        case 400: // ou cookie nao eh string ou nao existe declaracao previamente gerada
        case 401: // ou sem cookie ou cookie outdated
        case 501: // erro no parse da resposta da ufrgs
        case 502: // erro de network com a ufrgs
        case 500: // erro generico, tratar junto com default
        default: // nao deve ser possivel
      }
    }
    if (this.firstEnrollmentRequest) {
      this.firstEnrollmentRequest = false
      link()
    }
  }

  doRenderStuff() { // temp para chamar as minhas funcoes
    if (this.state.isLoggedIn) {
      if (!this.state.requestCode) {
        this.initializeRequestCode()
      } else {
        this.retrieveRequestCode()
      }
      this.getEnrollmentDeclaration()
      return this.calendarz.map((item) => <Calendar calendario={item} comprovante = {"http://www.google.com"}/>)
      // return <Calendar calendario={this.state.calendars} comprovante = {this.state.enrollmentDeclarationLink}/>
    } else {
      return <Login loginCallback={(isLoggedIn) => this.setState({...this.state, isLoggedIn: isLoggedIn, enrollmentDeclarationLink: "google.com"})}/>
    }
  }

  render() {
    console.log(this.state.isLoggedIn)
    return (
        <>
          {this.doRenderStuff()}
        </>
    );
  }
}

export default App;
