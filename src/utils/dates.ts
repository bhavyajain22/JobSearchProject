export function parseISOOrNull(s?: string) {
  if (!s) return null;
  const d = new Date(s);
  return isNaN(d.getTime()) ? null : d;
}

export function isWithinDays(dateISO?: string, days?: number) {
  if (!dateISO || !days) return true;
  const d = parseISOOrNull(dateISO);
  if (!d) return true;
  const now = Date.now();
  const cutoff = now - days * 24 * 60 * 60 * 1000;
  return d.getTime() >= cutoff;
}
