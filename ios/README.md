import SwiftUI

struct ContentView: View {
    @State private var currentDate = Date()
    @State private var events: [String: [String]] = [
        "2026-11-12": ["Spotkanie z klientem"],
        "2026-11-15": ["Urodziny", "Płatność"],
        "2026-11-20": ["Wizytacja"]
    ]

    private let calendar = Calendar.current

    private var monthFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "pl_PL")
        formatter.dateFormat = "LLLL yyyy"
        return formatter
    }

    private var weekdays: [String] {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "pl_PL")
        return formatter.shortStandaloneWeekdaySymbols
    }

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                HStack {
                    Button(action: { shiftMonth(-1) }) {
                        Image(systemName: "chevron.left")
                            .font(.title2)
                    }
                    Spacer()
                    Text(monthFormatter.string(from: currentDate).capitalized)
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

                LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 7), spacing: 10) {
                    ForEach(weekdays, id: \..self) { weekday in
                        Text(weekday)
                            .font(.caption)
                            .foregroundStyle(.secondary)
                            .frame(maxWidth: .infinity)
                    }

                    ForEach(daysInMonth(), id: \..self) { item in
                        let date = item.date
                        let dateKey = ISO8601DateFormatter().string(from: date)
                        let dayEvents = events[dateKey] ?? []

                        Button {
                            let eventLabel = "Nowe wydarzenie"
                            var updated = events[dateKey] ?? []
                            updated.append(eventLabel)
                            events[dateKey] = updated
                        } label: {
                            VStack(alignment: .leading) {
                                Text("\(item.day)")
                                    .font(.system(size: 15, weight: .semibold))
                                    .foregroundStyle(item.isCurrentMonth ? .primary : .secondary)
                                if !dayEvents.isEmpty {
                                    Circle()
                                        .fill(Color.orange)
                                        .frame(width: 8, height: 8)
                                }
                            }
                            .frame(maxWidth: .infinity, minHeight: 70)
                            .padding(4)
                            .background(item.isToday ? Color.blue.opacity(0.15) : Color.clear)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                        }
                    }
                }
                .padding()

                List {
                    ForEach(events.keys.sorted(), id: \..self) { dateKey in
                        if let items = events[dateKey], !items.isEmpty {
                            Section(header: Text(dateKey)) {
                                ForEach(items, id: \.self) { item in
                                    Text(item)
                                }
                            }
                        }
                    }
                }
                .listStyle(.insetGrouped)
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

    private func shiftMonth(_ delta: Int) {
        if let newDate = calendar.date(byAdding: .month, value: delta, to: currentDate) {
            currentDate = newDate
        }
    }

    private func daysInMonth() -> [CalendarCell] {
        let monthRange = calendar.range(of: .day, in: .month, for: currentDate) ?? 1..<32
        let firstDayOfMonth = calendar.date(from: calendar.dateComponents([.year, .month], from: currentDate)) ?? currentDate
        let firstWeekday = calendar.component(.weekday, from: firstDayOfMonth)
        let leadingDays = (firstWeekday + 6) % 7

        var cells: [CalendarCell] = []

        for index in 0..<(42) {
            let dayOffset = index - leadingDays
            let date = calendar.date(byAdding: .day, value: dayOffset, to: firstDayOfMonth) ?? currentDate
            let day = calendar.component(.day, from: date)
            let isCurrentMonth = calendar.component(.month, from: date) == calendar.component(.month, from: currentDate)
            let isToday = calendar.isDate(date, inSameDayAs: Date())
            cells.append(CalendarCell(day: day, date: date, isCurrentMonth: isCurrentMonth, isToday: isToday))
        }

        return cells
    }
}

private struct CalendarCell: Identifiable, Hashable {
    let id = UUID()
    let day: Int
    let date: Date
    let isCurrentMonth: Bool
    let isToday: Bool
}

#Preview {
    ContentView()
}
