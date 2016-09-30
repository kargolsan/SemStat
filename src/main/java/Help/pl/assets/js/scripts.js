// create the module and name it scotchApp
        // also include ngRoute for all our routing needs
    var scotchApp = angular.module('ITGoloAPP', ['ngRoute']);

    // configure our routes
    scotchApp.config(function($routeProvider) {
        $routeProvider

            // route for the home page
            .when('/', {
                templateUrl : 'pages/home.html',
                controller  : 'mainController'
            })

            // route for the about page
            .when('/about', {
                templateUrl : 'pages/about.html',
                controller  : 'aboutController'
            })

            // route for the contact page
            .when('/contact', {
                templateUrl : 'pages/contact.html'
            })

            .when('/licenses', {
                 templateUrl : 'pages/licenses.html'
            })

            .when('/phone_email', {
                 templateUrl : 'pages/phone_email.html'
            });

    });

    scotchApp.controller('mainController', function($scope) {
    });

    scotchApp.controller('aboutController', function($scope) {
    });
