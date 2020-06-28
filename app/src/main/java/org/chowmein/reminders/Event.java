package org.chowmein.reminders;

import java.util.Date;

class Event {
  private Date date;
  private String desc;
  private int dbr;
  private boolean isSelected;

  public Event(Date date, String desc, int dbr) {
    this.date = date;
    this.desc = desc;
    this.dbr = dbr;
  }

  public Date getDate() {
    return date;
  }
  
  public void setDate(Date date) {
    this.date = date;
  }

  public int getDbr() {
    return dbr;
  }

  public void setDbr(int dbr) {
    this.dbr = dbr;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public boolean isSelected() { return this.isSelected; }

  public void setSelected(boolean selected) { this.isSelected = selected; }

  @Override
  public boolean equals(Object o) {
    if(this == o) return true;
    if(o == null || !(o instanceof Event)) return false;

    Event other = (Event) o;
    return date.equals(other.getDate()) &&
           desc.equals(other.getDesc()) &&
           dbr == other.getDbr();
  }

  @Override
  public String toString() {
      return "date: " + this.date +
             ", desc: " + this.desc +
             ", dbr: " + this.dbr;
  }
}