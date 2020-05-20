package com.badoo.ribs.clienthelper

import com.jakewharton.rxrelay2.Relay

interface Connectable<Input, Output> {
    val input: Relay<Input>
    val output: Relay<Output>
}

