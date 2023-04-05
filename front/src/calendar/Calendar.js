import React, {Component} from 'react';
import {DayPilot, DayPilotCalendar} from "daypilot-pro-react";


const days = {
  seg: "2023-04-03T",
  ter:"2023-04-04T",
  qua:"2023-04-05T",
  qui:"2023-04-06T",
  sex:"2023-04-07T",
}

const createEventTimes = (scheduledTime, name) => ({
  start: days[scheduledTime.shortDay] + scheduledTime.startTime + ":00",
  end: days[scheduledTime.shortDay] + scheduledTime.endTime + ":00",
  text: name,
})

const createEventsFromCadeira = (cadeira) => {
  return cadeira.scheduledTimes.map(scheduledTime => createEventTimes(scheduledTime, cadeira.name) )
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

        const events = this.state.events ?? []

        const event = this.props.calendario.find(
         (item) => {
           return item.name === title
         }
        )

        console.log({event})
        this.openModal(event)

      },
    };
  }

  componentDidMount() {

    const calendario = this.props?.calendario ?? [[]]

    const events = this.props.calendario.flatMap(
        cadeira => createEventsFromCadeira(cadeira)
    )

    console.log({calendario, events})


    this.setState({
      startDate: "2023-04-03",
      events,
      modal: this.state.modal
    });

  }

  get calendar() {
    return this.calendarRef.current.control;
  }


  render() {
    const isModalOpen = this.state.modal?.isModalOpen;
    console.log(this.state)
    return (
      <div>
        {isModalOpen && <>
          <button className="layer" onClick={this.closeModal}/>
          <div className="modal">
            {console.log("augusto olha aqui =>>>>", this.state.modal.content)}
            {/*    TODO ARRUMAR AQUI*/}
        </div>
        </>}
        <DayPilotCalendar
          {...this.state}
          ref={this.calendarRef}
        />
      </div>
    );
  }
}

export default Calendar;
