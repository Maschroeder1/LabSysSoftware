import React, {Component} from 'react';
import './App.css';
import Calendar from "./calendar/Calendar";
import {Login} from "./login";

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

  constructor(props) {
    super(props);
    this.state = {
      isLoggedIn: false,
    }
  }

  render() {
    console.log(this.state.isLoggedIn)
    return (
        <>
          {this.state.isLoggedIn ?
              this.calendars.map((item) => <Calendar calendario={item}/>)
              : <Login loginCallback={(isLoggedIn) => this.setState({isLoggedIn})}/>

          }

        </>
    );
  }
}

export default App;
