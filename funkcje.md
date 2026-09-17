1. Główne opcje programu
Siatka Główna Kalendarza: Wyświetlanie dni miesiąca z możliwością dynamicznego przełączania widoków. Każda komórka wskazuje wydarzenia użytkownika oraz święta międzynarodowe.
Dynamiczne Motywy Kalendarzy: Interfejs dostosowuje swój styl wizualny (tło, kolorystykę i elementy dekoracyjne) w zależności od aktywnego systemu kalendarzowego (np. motyw tradycyjny dla kalendarza islamskiego, chińskiego lub gregoriańskiego).
Obsługa Kalendarzy Świata: Integracja z zewnętrznym API (np. Nager.Date) w celu pobierania i filtrowania świąt państwowych oraz religijnych dla wielu krajów jednocześnie.
Przelicznik Systemów Dat: Możliwość podglądu równoległych dat z alternatywnych systemów rachuby czasu (np. kalendarz księżycowy, Hijri, żydowski).
Zarządzanie Wydarzeniami: Pełna obsługa tworzenia, edycji i usuwania prywatnych notatek, wpisów oraz przypomnień w lokalnej bazie danych.

2. Przepływ sterowania (Flow)

A. Uruchomienie Aplikacji
1. Inicjalizacja lokalnej bazy danych (Room).
2. Sprawdzenie połączenia z siecią i pobranie aktualnych danych o świętach dla wybranej listy krajów.
3. Załadowanie preferowanego motywu wizualnego na podstawie zapisanych ustawień.

B. Ekran Główny (Siatka Miesiąca)
1. Prezentacja pełnego miesiąca z nakładką symboli i ikon dla ważnych dni.
2. Kliknięcie w konkretny dzień otwiera listę ze szczegółami wydarzeń i świąt przypisanych do tej daty.
3. Przycisk dodawania (`+`) przekierowuje do formularza nowego wpisu.

C. Ekran Dodawania / Edycji Wydarzenia
1. Wprowadzenie tytułu, opisu, daty oraz godziny.
2. Przypisanie wydarzenia do konkretnej kategorii lub kalendarza.
3. Zapisanie danych do bazy Room i odświeżenie widoku głównego.

D. Ekran Ustawień
1. Wybór krajów, z których mają być pobierane święta.
2. Przełączanie widoczności systemów kalendarzowych (gregoriański, islamski, chiński itd.).
3. Zmiana motywu graficznego (automatyczny na podstawie kalendarza / jasny / ciemny).
