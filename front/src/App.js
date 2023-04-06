import React, {Component} from 'react';
import './App.css';
import Calendar from "./calendar/Calendar";
import {Login} from "./login";
import {initializeClasses, retrieveClasses, retrieveEnrollmentDeclaration} from "./requests";

class App extends Component {

  const test = {
    "message": "Ok",
    "content": {
      "CLASS 1 - (CODE1)": {
        "timeslots": [
          {
            "classIdentifier": "U",
            "availableSlots": 30,
            "professors": [
              "PROFESSOR NAME"
            ],
            "scheduledTimes": [
              {
                "day": "Quarta",
                "startTime": "08:30",
                "endTime": "11:50",
                "location": "SALA DE AULA 123 - PRÉDIO 456 4",
                "locationMap": null
              }
            ]
          }
        ],
        "credits": 4
      },
      "CLASS 2 (CODE2)": null
    }
  }
  calendars = [
    [
      {
        "name": "Tecnica de construcao de Programas",
        "classIdentifier": "U",
        "availableSlots": 30,
        "credits": 4,
        "professors": [
          "PROFESSOR NAME"
        ],
        "scheduledTimes": [
          {
            "shortDay": "qua", // suggestion
            "day": "Quarta",
            "startTime": "08:10",
            "endTime": "11:50",
            "location": "SALA DE AULA 123 - PRÉDIO 456 4",
            "locationMap": null
          },
          {
            "shortDay": "seg", // suggestion
            "day": "Segunda",
            "startTime": "08:30",
            "endTime": "11:50",
            "location": "SALA DE AULA 123 - PRÉDIO 456 4",
            "locationMap": null
          }
        ]
      },
      {
        "name": "Circutios Doidos",
        "classIdentifier": "U",
        "availableSlots": 30,
        "credits": 4,
        "professors": [
          "PROFESSOR NAME"
        ],
        "scheduledTimes": [
          {
            "shortDay": "ter", // suggestion
            "day": "Terca",
            "startTime": "08:30",
            "endTime": "11:50",
            "location": "SALA DE AULA 123 - PRÉDIO 456 4",
            "locationMap": null
          },
          {
            "shortDay": "qui", // suggestion
            "day": "Quinta",
            "startTime": "08:30",
            "endTime": "11:50",
            "location": "SALA DE AULA 123 - PRÉDIO 456 4",
            "locationMap": null
          }
        ]
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
      enrollmentDeclarationLink: ''
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
        console.log("Resultado parcial")
        console.log(respose.content.content) // probs setState?
        await sleep() // 1 query/sec
        respose = await retrieveClasses(this.state.requestCode)
      }

      switch (respose.statusCode) {
        case 200:
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
      return this.calendars.map((item) => <Calendar calendario={item}/>)
    } else {
      return <Login loginCallback={(isLoggedIn) => this.setState({...this.state, isLoggedIn: isLoggedIn})}/>
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
