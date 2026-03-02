import { format, differenceInDays, parseISO, startOfWeek, endOfWeek } from 'date-fns';
import { ko } from 'date-fns/locale';

export function formatDate(isoString) {
  if (!isoString) return '';
  return format(parseISO(isoString), 'yyyy년 M월 d일', { locale: ko });
}

export function formatShortDate(isoString) {
  if (!isoString) return '';
  return format(parseISO(isoString), 'M월 d일', { locale: ko });
}

export function formatDDay(days) {
  if (days === 0) return 'D-Day';
  if (days > 0) return `D-${days}`;
  return `D+${Math.abs(days)}`;
}

export function getWeekLabel(weekStart, weekEnd) {
  if (!weekStart) return '';
  const start = formatShortDate(weekStart);
  const end = weekEnd ? formatShortDate(weekEnd) : '';
  return end ? `${start} ~ ${end}` : start;
}

export function toISODate(date) {
  return format(date, 'yyyy-MM-dd');
}

export function getMondayOfCurrentWeek() {
  const monday = startOfWeek(new Date(), { weekStartsOn: 1 });
  return toISODate(monday);
}

export function getWeekDays(weekStart) {
  const start = parseISO(weekStart);
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date(start);
    d.setDate(start.getDate() + i);
    return toISODate(d);
  });
}

export function formatDateTime(isoString) {
  if (!isoString) return '';
  return format(parseISO(isoString), 'yyyy.MM.dd HH:mm', { locale: ko });
}
