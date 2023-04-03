import React, {Component} from 'react';
import {DayPilot, DayPilotCalendar, DayPilotNavigator} from "@daypilot/daypilot-lite-react";
import "./CalendarStyles.css";

const styles = {
  wrap: {
    display: "flex"
  },
  left: {
    marginRight: "10px"
  },
  main: {
    flexGrow: "1"
  }
};

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

  constructor(props) {
    super(props);
    this.calendarRef = React.createRef();
    this.state = {
      viewType: "Week",
      cellDuration: 10,
      cellHeight: 30,
      dayBeginsHour: 9,
      dayEndsHour: 23,
      timeRangeSelectedHandling: "Disabled",
      eventDeleteHandling: "Disabled",
      eventMoveHandling: "Update",
      onEventMoved: (args) => {
        args.control.message("Event moved: " + args.e.text());
      },
      eventResizeHandling: "Update",
      onEventResized: (args) => {
        args.control.message("Event resized: " + args.e.text());
      },
      eventClickHandling: "Disabled",
      eventHoverHandling: "Disabled",
    }
  }

  get calendar() {
    return this.calendarRef.current.control;
  }

  colors = [ // @todo

  ]




  componentDidMount() {

    // console.log(this.props.calendario[0])
    // console.log(createEventsFromCadeira(this.props.calendario[0]))

    const calendario = this.props?.calendario ?? [[]]

    const events = this.props.calendario.flatMap(
        cadeira => createEventsFromCadeira(cadeira)
    )

    console.log({calendario, events})

    const events2 = [
      {
        id: 1,
        text: "Event 1",
        start: "2023-03-07T10:30:00",
        end: "2023-03-07T13:00:00"
      },
      {
        id: 2,
        text: "Event 2",
        start: "2023-03-08T09:30:00",
        end: "2023-03-08T11:30:00",
        backColor: "#6aa84f"
      },
      {
        id: 3,
        text: "Event 3",
        start: "2023-03-08T12:00:00",
        end: "2023-03-08T15:00:00",
        backColor: "#f1c232"
      },
      {
        id: 4,
        text: "Event 4",
        start: "2023-03-06T11:30:00",
        end: "2023-03-06T14:30:00",
        backColor: "#cc4125"
      },
    ];

    const startDate = "2023-04-03";

    this.calendar.update({startDate, events});

  }

  render() {
    return (
      <div style={styles.wrap}>
        <div style={styles.main}>
          <DayPilotCalendar
            {...this.state}
            ref={this.calendarRef}
          />
        </div>
      </div>
    );
  }
}

export default Calendar;
