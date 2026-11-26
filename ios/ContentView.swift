import SwiftUI

struct ContentView: View {
    @State private var currentDate = Date()
    @State private var events: [String: [String]] = [
        "2026-11-12": ["Spotkanie z klientem"],
        "2026-11-15": ["Urodziny", "Płatność"],
        "2026-11-20": ["Wizytacja"]
    ]

    // Kalendarz z poniedziałkiem jako pierwszym dniem tygodnia
    private var calendar: Calendar {
        var cal = Calendar(identifier: .gregorian)
        cal.locale = Locale(identifier: "pl_PL")
        cal.firstWeekday = 2
        return cal
    }

    private static let monthFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "pl_PL")
        formatter.dateFormat = "LLLL yyyy"
        return formatter
    }()

    private static let keyFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter
    }()

    // Pn, Wt, Śr, Cz, Pt, So, Nd
    private var weekdays: [String] {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "pl_PL")
        let symbols = formatter.shortStandaloneWeekdaySymbols ?? []
        guard symbols.count == 7 else { return [] }
        return Array(symbols[1...]) + [symbols[0]]
    }

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                header
                calendarGrid
                eventList
            }
            .navigationTitle("Kalendarz")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Dziś") {
                        currentDate = Date()
                    }
                }
            }
        }
    }

    // MARK: - Widoki

    private var header: some View {
        HStack {
            Button(action: { shiftMonth(-1) }) {
                Image(systemName: "chevron.left")
                    .font(.title2)
            }
            Spacer()
            Text(Self.monthFormatter.string(from: currentDate).capitalized)
                .font(.title2.bold())
            Spacer()
            Button(action: { shiftMonth(1) }) {
                Image(systemName: "chevron.right")
                    .font(.title2)
            }
        }
        .padding()
        .background(Color(.systemTeal))
        .foregroundStyle(.white)
    }

    private var calendarGrid: some View {
        LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 7), spacing: 10) {
            ForEach(weekdays, id: \.self) { weekday in
                Text(weekday)
                    .font(.caption)
                    .foregroundStyle(.secondary)
                    .frame(maxWidth: .infinity)
            }

            ForEach(daysInMonth()) { item in
                let dateKey = Self.keyFormatter.string(from: item.date)
                let hasEvents = !(events[dateKey] ?? []).isEmpty

                Button {
                    events[dateKey, default: []].append("Nowe wydarzenie")
                } label: {
                    VStack(alignment: .leading) {
                        Text("\(item.day)")
                            .font(.system(size: 15, weight: .semibold))
                            .foregroundStyle(item.isCurrentMonth ? .primary : .secondary)
                        if hasEvents {
                            Circle()
                                .fill(Color.orange)
                                .frame(width: 8, height: 8)
                        }
                    }
                    .frame(maxWidth: .infinity, minHeight: 50, alignment: .topLeading)
                    .padding(4)
                    .background(item.isToday ? Color.blue.opacity(0.15) : Color.clear)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                .buttonStyle(.plain)
            }
        }
        .padding()
    }

    private var eventList: some View {
        List {
            ForEach(events.keys.sorted(), id: \.self) { dateKey in
                if let items = events[dateKey], !items.isEmpty {
                    Section(header: Text(dateKey)) {
                        ForEach(Array(items.enumerated()), id: \.offset) { _, item in
                            Text(item)
                        }
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
    }

    // MARK: - Logika

    private func shiftMonth(_ delta: Int) {
        if let newDate = calendar.date(byAdding: .month, value: delta, to: currentDate) {
            currentDate = newDate
        }
    }

    private func daysInMonth() -> [CalendarCell] {
        let firstDayOfMonth = calendar.date(from: calendar.dateComponents([.year, .month], from: currentDate)) ?? currentDate
        let firstWeekday = calendar.component(.weekday, from: firstDayOfMonth) // 1 = niedziela
        let leadingDays = (firstWeekday + 5) % 7 // poniedziałek = 0
        let currentMonth = calendar.component(.month, from: currentDate)

        return (0..<42).map { index in
            let date = calendar.date(byAdding: .day, value: index - leadingDays, to: firstDayOfMonth) ?? currentDate
            return CalendarCell(
                day: calendar.component(.day, from: date),
                date: date,
                isCurrentMonth: calendar.component(.month, from: date) == currentMonth,
                isToday: calendar.isDateInToday(date)
            )
        }
    }
}

private struct CalendarCell: Identifiable {
    var id: Date { date }
    let day: Int
    let date: Date
    let isCurrentMonth: Bool
    let isToday: Bool
}

#Preview {
    ContentView()
}
