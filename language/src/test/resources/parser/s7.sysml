package 'Comment Example' {
  /* This is documentary comment, part of the model,
   * annotating (by default) it's owning package. */

  comment Comment1 /* This is a named comment. */

  comment about Automobile
  /* This is an unnamed documentary comment,
   * annotating an explicitly specified element.
   */

  part def Automobile;

  /**
   * This is a documentary comment, annotating the
   * following element.
   */
  alias Automobile as Car;

  // This is a note. It is in the text, but not part
  // of the model.
  import ISO::TorqueValue as Torque;
}