import React, {Component} from 'react';
import {DayPilotCalendar} from "daypilot-pro-react";
import "./calendar.css"

const days = {
  seg: "2023-04-03T",
  ter:"2023-04-04T",
  qua:"2023-04-05T",
  qui:"2023-04-06T",
  sex:"2023-04-07T",
}

const createEventTimes = (scheduledTime, name, locationMap, classPlan) => ({
  start: days[scheduledTime.shortDay] + scheduledTime.startTime + ":00",
  end: days[scheduledTime.shortDay] + scheduledTime.endTime + ":00",
  text: name,
  locationMap,
  classPlan,
})

const createEventsFromTurma = (cadeira, name, classPlan) => {
  return cadeira.scheduledTimes.map(scheduledTime => createEventTimes(scheduledTime, name, scheduledTime.locationMap, classPlan) )
}

const createActualTurma = (calendario) => {
  return calendario
    .filter(cadeira => { return !!cadeira.timeslots })
    .flatMap(cadeira => {
      return cadeira.timeslots.map(turma => {
        const name = cadeira.name + ' ' + turma.classIdentifier
        return {
          availableSlots: turma.availableSlots,
          professors: turma.professors,
          name,
          events: createEventsFromTurma(turma, name, cadeira.classPlan),
          isOnCalendar: false,
          classPlan: cadeira.classPlan,
        }
      })
  })
}

class Calendar extends Component {


  closeModal = () => this.setState({...this.state, modal: {isModalOpen: false, content: ""}});

  openModal = (content) => {
    this.setState({...this.state, modal: {isModalOpen: true, content}})
  };

  constructor(props) {
    super(props);
    this.calendarRef = React.createRef();
    this.state = {
      viewType: "Week",
      headerDateFormat: "dddd",
      cellDuration: 10,
      businessBeginsHour: 7,
      businessEndsHour: 23,
      dayBeginsHour: 7,
      dayEndsHour: 23,
      cellHeight: 10,
      timeRangeSelectedHandling: "Disabled",
      eventDeleteHandling: "Disabled",
      eventMoveHandling: "Disabled",
      eventResizeHandling: "Disabled",
      eventHoverHandling: "Disabled",
      eventClickHandling: "Enabled",
      onEventClicked: (args) => {
        const title = args.e.text();


        const event = this.state.tchurmas.find(
         (item) => {
           return item.name === title
         }
        )
        this.openModal(event)

      },
    };
  }

  componentDidUpdate(prevProps) {
    if (this.props.calendario !== prevProps.calendario) {
      const calendario = this.props?.calendario ?? [[]]
      const tchurmas = createActualTurma(calendario)

      this.setState({...this.state, tchurmas});
    }
  }

  componentDidMount() {

    console.log( {state: this.state, props: this.props})

    const calendario = this.props?.calendario ?? [[]]


    const events = []

    const tchurmas = createActualTurma(calendario)



    this.setState({
      startDate: "2023-04-03",
      tchurmas,
      events,
      modal: this.state.modal
    });

  }

  get calendar() {
    return this.calendarRef.current.control;
  }


  render() {
    const isModalOpen = this.state.modal?.isModalOpen;
    const modalContent = this.state.modal?.content;

    return (
        <>
          {isModalOpen && modalContent && <>
            <button className="layer" onClick={this.closeModal}/>
            <div className="modal">
              <p>{modalContent.name}</p>
              <p>vagas: {modalContent.availableSlots}</p>
              <p>professores</p>
              <ul>{modalContent.professors.map(professor => <li>{professor}</li>)}</ul>
              <div>Local: <a href={modalContent.events[0].locationMap}>Link</a> </div>
              <div>Plano de ensino: <a href={modalContent.events[0].classPlan}>Link</a> </div>


              <button onClick={() => {
                const newTchurmas = this.state.tchurmas.map(item => item)
                const newTchurma = newTchurmas.find(item => item.name === this.state.modal.content.name)
                newTchurma.isOnCalendar = false;

                const newEvents = this.state.events.filter(item => {
                  return item.text !== this.state.modal.content.name
                })

                this.setState({
                  ...this.state,
                  tchurmas: newTchurmas,
                  modal: {isModalOpen: false, content: ""},
                  events: newEvents,
                })
              }
              }>
                Remover
              </button>
            </div>
          </>}
          <div className="wrapper">
            <div className="menu">
              {
                !!this.props.comprovante &&
                <div>
                  <button
                      onClick={() => {
                        window.open(this.props.comprovante, '_blank', 'noreferrer');
                      }
                      } className="tchurma-button">{"Download histórico de matrícula"}</button>
                </div>
              }
              {this.state?.tchurmas?.map(classe => {

                return <div>
                  <button
                      disabled={classe.isOnCalendar}
                      onClick={() => {
                        classe.isOnCalendar = true;
                    this.setState({...this.state,
                      tchurmas: [...this.state.tchurmas],
                      events: [...classe.events, ...this.state.events]})
                  }
                  } className="tchurma-button">{classe.name}</button>
                </div>

              }) }
            </div>
            <DayPilotCalendar
              {...this.state}
              ref={this.calendarRef}
            />
        </div>
      </>
    );
  }
}

export default Calendar;
