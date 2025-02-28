package com.example.LibrarySystemWebApplication.web;

import com.example.LibrarySystemWebApplication.dao.BorrowingDao;
import com.example.LibrarySystemWebApplication.dao.LibraryElementDao;
import com.example.LibrarySystemWebApplication.model.Book;
import com.example.LibrarySystemWebApplication.model.LibraryElement;
import com.example.LibrarySystemWebApplication.model.Movie;
import com.example.LibrarySystemWebApplication.Utility;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "libraryElementServlet", value = "/libraryElement")
public class LibraryElementServlet extends HttpServlet implements DataInputHelper {

    private static final LibraryElementDao libraryElementDao = new LibraryElementDao();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = (String)request.getAttribute("action");

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader ("Expires", 0);

        switch (action) {
            case "showList":
                libraryElementList(request, response);
                break;
            case "search":
                searchedResultsLibraryElements(request, response);
                break;
            case "editLibraryElement":
                showEditFrom(request, response);
                break;
            case "delete":
                doPost(request, response);
                break;
            case "newLibraryElement":
                addLibraryElement(request, response);
                break;
            default:

                break;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = (String)request.getAttribute("action");

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader ("Expires", 0);

        switch (action) {
            case "update":
                updateLibraryElement(request, response);
                break;
            case "delete":
                deleteLibraryElement(request, response);
                break;
            case "insert":
                insertLibraryElement(request, response);
                break;
            default:
                break;
        }

    }

    private void libraryElementList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<LibraryElement> libraryElementsList = new ArrayList<LibraryElement>();
        libraryElementsList = libraryElementDao.getAllLibraryElements();
        request.setAttribute("libraryElementList", libraryElementsList);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("libraryElement/searcher.jsp");
        requestDispatcher.forward(request, response);

    }

    private void searchedResultsLibraryElements(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("searchedTitle");

        List<LibraryElement> libraryElementList = libraryElementDao.getLibraryElementsByTitle(title);
        request.setAttribute("libraryElementList", libraryElementList);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("libraryElement/searcher.jsp");
        requestDispatcher.forward(request, response);

    }

    private void showEditFrom(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int libraryElementId = Integer.parseInt(request.getParameter("libraryElementId"));
        LibraryElement libraryElement = libraryElementDao.getLibraryElementById(libraryElementId);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("libraryElement/libraryElementForm.jsp");
        request.setAttribute("isDataUpdating", true);
        request.setAttribute("libraryElement", libraryElement);
        requestDispatcher.forward(request, response);

    }

    private void updateLibraryElement(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<String> errorMessageList = new ArrayList<>();
        boolean isDataIncorrect = false;
        LibraryElement libraryElement = null;

        int libraryElementId = Integer.parseInt(request.getParameter("libraryElementId"));
        String title = request.getParameter("title");
        int sortId = Integer.parseInt(request.getParameter("sortId"));
        int statusId = Integer.parseInt(request.getParameter("statusId"));
        byte typeId = Byte.parseByte(request.getParameter("typeId"));
        String detailedInfo = request.getParameter("detailedInfo");

        if (validateIntData(detailedInfo)) {
            if (typeId == Utility.TYPE_BOOK) {
                int pagesNumber = Integer.parseInt(detailedInfo);
                libraryElement = new Book(libraryElementId, typeId, title, sortId, statusId, pagesNumber);
            } else if (typeId == Utility.TYPE_MOVIE) {
                int durationTime = Integer.parseInt(detailedInfo);
                libraryElement = new Movie(libraryElementId, typeId, title, sortId, statusId, durationTime);
            }
        }

        if (!validateIntData(detailedInfo)) {
            detailedInfo = "";
        }
        if (!validateStringData(title)) {
            title = "";
        }

        errorMessageList = getErrorMessages(title, detailedInfo);

        if (!errorMessageList.isEmpty()) {
            isDataIncorrect = true;
            request.setAttribute("isDataIncorrect", isDataIncorrect);
            request.setAttribute("isDataUpdating", true);
            request.setAttribute("title", title);
            request.setAttribute("detailedInfo", detailedInfo);
            request.setAttribute("errorMessageList", errorMessageList);
            showEditFrom(request, response);
            return;
        }

        LibraryElementDao.updateLibraryElement(libraryElement);
        libraryElementList(request, response);

    }

    private void deleteLibraryElement(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int libraryElementId = Integer.parseInt(request.getParameter("libraryElementId"));

        libraryElementDao.acceptDeleteLibraryElement(libraryElementId);

        libraryElementList(request, response);

    }

    private void addLibraryElement(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("libraryElement/libraryElementForm.jsp");
        requestDispatcher.forward(request, response);

    }

    private void insertLibraryElement(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        List<String> errorMessageList = new ArrayList<>();
        boolean isDataIncorrect = false;
        LibraryElement libraryElement = null;

        String detailedInfo = request.getParameter("detailedInfo");
        String title = request.getParameter("title");
        byte typeId = Byte.parseByte(request.getParameter("typeId"));
        int sortId = Integer.parseInt(request.getParameter("sortId"));
        if (validateIntData(detailedInfo)) {
            if (typeId == Utility.TYPE_BOOK) {
                int pagesNumber = Integer.parseInt(detailedInfo);
                libraryElement = new Book(0, typeId, title, sortId, Utility.STATUS_AVAILABLE, pagesNumber);
            } else if (typeId == Utility.TYPE_MOVIE) {
                int durationTime = Integer.parseInt(detailedInfo);
                libraryElement = new Movie(0, typeId, title, sortId, Utility.STATUS_AVAILABLE, durationTime);
            }
        }

        if (!validateIntData(detailedInfo)) {
            detailedInfo = "";
        }
        if (!validateStringData(title)) {
            title = "";
        }

        errorMessageList = getErrorMessages(title, detailedInfo);

        if (!errorMessageList.isEmpty()) {
            isDataIncorrect = true;
            request.setAttribute("isDataIncorrect", isDataIncorrect);
            request.setAttribute("title", title);
            request.setAttribute("detailedInfo", detailedInfo);
            request.setAttribute("errorMessageList", errorMessageList);
            addLibraryElement(request, response);
            return;
        }

        libraryElementDao.insertLibraryElement(libraryElement);
        response.sendRedirect("index.jsp");
    }

    private List<String> getErrorMessages(String title, String detailedInfo) {

        List<String> errorMessageList = new ArrayList<>();

        if (DataInputHelper.checkEmpty(title) || DataInputHelper.isFirstCharEmpty(title)
                || DataInputHelper.checkLength(title)) {
            errorMessageList.add("Nieprawdiłowy tytuł!");
        }
        if (!DataInputHelper.isConvertableToInt(detailedInfo)) {
            errorMessageList.add("Nieprawdiłowy Liczba stron / Czas trwania!");
        }
        return errorMessageList;
    }

    private boolean validateIntData(String userData) {

        if (DataInputHelper.isConvertableToInt(userData)) {
            return true;
        }
        return false;
    }

    private boolean validateStringData(String userData) {

        if (DataInputHelper.checkEmpty(userData) || DataInputHelper.isFirstCharEmpty(userData)
                || DataInputHelper.checkLength(userData)) {
            return false;
        }
        return true;
    }


}