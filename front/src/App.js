import React, {Component} from 'react';
import './App.css';
import Calendar from "./calendar/Calendar";

class App extends Component {
  calendars = [
    [
      {
        "name": "Tecnica de construcao de Pirocas",
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
            "startTime": "08:30",
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
  ];
  render() {
    return (
        <>
          {this.calendars.map((item) => <Calendar calendario={item}/>)}
        </>
    );
  }
}

export default App;
