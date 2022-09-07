package Invalid {
  port def MyPort {
    final channel: Type;
  }
}
package Valid {
  part def MyPart {
    final value para: Type;
  }
  state def MyState(final para: Type);
}
