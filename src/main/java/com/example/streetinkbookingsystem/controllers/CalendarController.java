package com.example.streetinkbookingsystem.controllers;

import com.example.streetinkbookingsystem.services.BookingService;
import com.example.streetinkbookingsystem.services.CalendarService;
import com.example.streetinkbookingsystem.services.TattooArtistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;

@Controller
public class CalendarController {

    @Autowired
    CalendarService calendarService;
    @Autowired
    BookingService bookingService;
    @Autowired
    TattooArtistService tattooArtistService;

    // If there is passed date in the parameters then it display that month/year.
    // otherwise it will display the current month.
    @GetMapping("/calendar")
    public String seeCurrentMonth(Model model, @RequestParam(required = false) Integer year, @RequestParam(required = false)  Integer month) {

        // DUMMY USERNAME - skal ændres til den rigtig username
        String username = tattooArtistService.showTattooArtist().get(0).getUsername();
        /*
        HttpSession session = get session, if session is null then redirect to index.
         */
        model.addAttribute("username", username);

        //Initialize the calendar. If client gets to the calendar from another view
        //then show the current month. If they push the "next" or "previous" buttons then show that month.
        LocalDate date;
        if (year == null && month == null) {
             date = calendarService.getCurrentDate();
        } else
             date= LocalDate.of(year, month, 1); // start day is always the first in the month

        // Calculate the days in the month, get the weekNumbers, and calculate how many empty fills
        // there are needed before and after the dates of the months, so that the matrix is always
        // full, 6x7.
        ArrayList<LocalDate> daysInMonth = calendarService.getDaysInMonth(date.getYear(), date.getMonth());
        int[] weekNumbers = calendarService.getWeekNumbers(daysInMonth.get(0)); // calculate the week numbers based on the first date in the month
        int startFillers = calendarService.getEmptyStartFills(daysInMonth.get(0));
        int endFillers = calendarService.getEmptyEndFills(daysInMonth.get(0),daysInMonth);
        //Ad to model
        model.addAttribute("startFillers", startFillers);
        model.addAttribute("endFillers", endFillers);
        model.addAttribute("daysInMonth", daysInMonth);
        model.addAttribute("date", date);
        model.addAttribute("calendar", calendarService); //used in view to calculate how booked the day is
        model.addAttribute("weekNumbers", weekNumbers);
        model.addAttribute("username", username);
        return "home/calendar";
    }




    //finds the next month based on the month and year given
    //query parameters: passed along to the get mapping and displayed in the URL.
    // ? is the start of the query, & separates the parameters.
    @PostMapping("/calendar/next")
    public String seeNextMonth( @RequestParam Integer year, @RequestParam Integer month) {
        LocalDate nextDate = LocalDate.of(year, month, 1).plusMonths(1);
        return "redirect:/calendar?year=" + nextDate.getYear() + "&month=" + nextDate.getMonthValue();
    }


    //finds the previous month based on the month and year given
    @PostMapping("/calendar/previous")
    public String seePreviousMonth( @RequestParam Integer year, @RequestParam Integer month) {
        LocalDate previousDate = LocalDate.of(year, month, 1).minusMonths(1);
        return "redirect:/calendar?year=" + previousDate.getYear() + "&month=" + previousDate.getMonthValue();
    }
}
