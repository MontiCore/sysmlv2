action encAsyncMessage {
    action trigger accept  num: ScalarValues::Integer;
    action def Send(in target: ScalarValues::Integer);

    action aName  {
        in item inA = trigger::num;
        out item outA;
    }
    succession trigger then aName;
        then send Send(aName::outA) to Send::target;
}
