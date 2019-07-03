# Gyro
Gyro is a state management library that takes a functional approach in managing the mutable state (a.k.a. the single source of truth).

Instead of dispatching an `Action` through a series of sub-systems and reducers, an `Action` is represented as function that is supplied with the necessities for validating and updating the state. As a consequence, managaing state is much simpler and thereby more predictable.

Often the `Reducer` is seen as the silver bullet in state management, but in practice it is hard to track where actions are coming from and what they are changing. Therefore Gyro only works with functions that directly change the state.

The guide below explains the parts that make Gyro. All of the code snippets are available in the sample included in this repo. I recommend cloning and running it, since it explains the details that couldn't fit in this short document.

# TLDR
```kotlin
fun tldr(user: User = User.NotSignedIn()) {
    // 1. Observable source (could be: Rx.BehaviorSubject or LiveData)
    val emitter = Emitter(user)
    // 2. Create a store and use its destructure it
    val (observable, actions) = emitter.toStore(::UserActions)

    // 3. Subscribe to the given observable
    observable.subscribe { user -> println(user) }

    // 4. Call some actions
    actions.signIn("hello", "world")
    actions.signOut()
}
```
# Getting started
For this example we will be working with a state that is a `House` and it has a `Door` that has a `Bell` that can be ringing or not.
```kotlin
data class House(val door: Door = Door())

data class Door(val bell: Bell = Bell(), val isOpen: Boolean = false)

data class Bell(val isRinging: Boolean = false)
```

## Creating actions

One of the interactions we want to build is that of opening the door when it's closed and closing the door when it's open. In conventional Kotlin that means we have to create a copy of the door with a different `isOpen` state and a copy of the house of the new door. This code isn't the cleanest and Gyro fixes that by introducing a `Segment<House>`.
```kotlin
// Kotlin is creating nested copies, which is a great system, but the readability is not that great.
fun openDoor(house: House): House {
    return if (!house.door.isOpen)
        house.copy(door = house.door.copy(isOpen = true))
    else
        house
}

// Gyro reorganizes the copy method, which improves readability and allows segregation.
fun openDoor(house: Segment<House>) {
    house
        .select({ door }, { copy(door = it) })
        .filter { !isOpen }
        .select({ isOpen }, { copy(isOpen = it) })
        .set(true)
}
```
State management always consists of the steps: validating the current state and changing the state when is valid. In the above example we check whether the door is closed and open it when it is so.

Often opening the door is called from a user interaction, such a button click. Such UI is not responsible for supplying the `Segment` argument, so we'll wrap such actions in an object.

```kotlin
class Actions(private val segment: Segment<House>) : Actions {

    fun openDoor() {
        segment
            .select({ door }, { copy(door = it) })
            .filter { !isOpen }
            .select({ isOpen }, { copy(isOpen = it) })
            .set(true)
    }
    
}
```

Now when the UI needs to call it:
```kotlin
class DoorFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val openDoorButton = view.findViewById<Button>(R.id.openDoor)

        openDoorButton.setOnClickListener { actions.openDoor() } // calls interaction
        /*...*/
    }
}
```
## Setting up listeners

A `Segment` is responsible for maintaining state changes, but UI is only interested in listening to those changes. Therefore a Segment can easily obtained from a observable source such as a Rx's `BehaviorSubject` and `ReplaySubject` or Android's `MutableLiveData`. If you don't want to add additional dependencies; Gyro includes an observable source called `Emitter`, which has the popular operators from Rx.

```kotlin
fun subjectExample(house: House) {
    val subject = BehaviorSubject.createDefault(house)
    val segment: OptionalSegment<House> = subject.toOptionalSegment()
    val state: House? = subject.value

    subject.onNext(House())
}

fun liveDataExample(house: House) {
    val data = MutableLiveData<House>().apply { value = House() }
    val segment: OptionalSegment<House> = data.toOptionalSegment()
    val state: House? = data.value

    data.value = House()
}

fun emitterExample(house: House) {
    val emitter = Emitter(house)
    val segment: Segment<House> = emitter.toSegment()
    val state: House = segment.get()

    emitter.set(House())
}
```

The above examples all create the same functionality. The `Subject`, `LiveData` and `Emitter` distribute state changes and have operators like `map`, `filter` and `distinctUntilChanged`. All of them have an extension function to create a `Segment`, which is responsible for changing the state. 

Do notice that since `Emitter` is written in Kotlin it has has null-safety. Therefore it is the only one that produces a `Segment` instead of a `OptionalSegment`.

With a way of observing state changes and applying state changes, the UI can become fully interactive:

```kotlin
class DoorFragment : Fragment() {
    /*...*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val openDoorButton = view.findViewById<Button>(R.id.openDoor)

        openDoorButton.setOnClickListener { actions.openDoor() } // calls interaction

        data
            .map { house -> !house.door.isOpen }
            .distinctUntilChanged()
            .subscribe { isClosed -> openDoorButton.isEnabled = isClosed } // gets updates
    }
}
```

## Building a store

The graphical part of an application is only interested in the state and how it can change that state. Gyro contains utility to bundle those for easy distribution in a object that is called a `Store`. 
```kotlin
    private val store: LiveDataStore<State, Actions> = 
        MutableLiveData(State())
            .toLiveDataStore(actionsFactory = { segment: Segment<State> -> Actions(segment) })
            
    val data: LiveData<State> = store.data
    val actions: Actions = store.actions
```

Such `Store` is simply a container for holding the actions and the observable source. The above example shows how to create a `Store` for `LiveData`, but there are also options for `BehaviorSubject`, `ReplaySubject` and Gyro's own `Emitter`.

# Asynchronicity
When implementing a sign-in functionality, we let the lengthy network call happen on a background thread. During its operation we show a spinner and on success we show the screen behind the sign-in screen. 

It is important that, when building such functionality, you have clear in which states your system (read: `Segment`) can be and what action can trigger a state change. This can be modeled in a (simplified) transition table:

| # 	| Current State 	| Action  	| New State   	|
|---	|---------------	|---------	|-------------	|
| 1 	| NotSignedIn   	| signIn  	| SigningIn   	|
| 2 	| SigningIn     	| success 	| SignedIn    	|
| 3 	| SigningIn     	| failure 	| NotSignedIn 	|
| 4 	| SignedIn      	| signOut 	| NotSignedIn 	|

Each action is considered a function without a returning value and potentially some arguments. The `signIn` for instance will contain the `name` and `password` of the user. 

For simplicity, the sign-in network call will receive a callback for its success and for its failure and both of them will progress the state as it is shown in the table above. It could have returned an `Single<String>` or been a suspending function, but again: simplicity.

```kotlin
sealed class User {
    data class NotSignedIn(val reason: String? = null) : User()
    object SigningIn : User()
    data class SignedIn(val token: String) : User()
}

private fun someNetworkSignIn(
    name: String,
    pass: String,
    onSuccess: (token: String) -> Unit,
    onFailure: (error: Throwable) -> Unit
) { /*...*/ }

class UserActions(private val segment: Segment<User>) {

    private var user by segment.toProperty()

    fun signIn(name: String, pass: String) {
        segment
            .filter<User.NotSignedIn>()
            .update { User.SigningIn }
            ?.let { someNetworkSignIn(name, pass, ::success, ::failure) }
    }

    private fun success(token: String) {
        user = User.SignedIn(token)
    }

    private fun failure(error: Throwable) {
        user = User.NotSignedIn(error.message)
    }

    fun signOut() {
        user = User.NotSignedIn()
    }

}
```
Within the `signIn` function, the network sign-in is only called when `update` call returns a non-null value. It will do that only if its segment is valid, or in other words: When the user is not signed in.

Now the system is in a `SigningIn` state and can progress by a call of the `success` or `failure` function. Both of them update the state by setting the `user` property, which will call `Segment.set`.

# Side effects
When having a production app, it can happen that you want to extend the functionality of a `Segment` without altering its signature. These type of functions are often called `MiddleWare` or side effects. In Gyro they are called `Interceptor` since it intercepts the getter and the setter of a state. 

Rarely it is necessary to intercept the getter, so there is a convenience overload for intercepting a setter. The example below demonstrates how an interception can check whether the current state differs from the newly created state and only set the value when it changed.

```kotlin
fun <T> Segment<T>.checkEquality(): Segment<T> =
    intercept { set: (T) -> Unit ->
    
        { value ->
            if (value != this@checkEquality.get()) {
                set(value)
            }
        }
    }
```
# Threading
The `Segment<T>` is thread safe, since it does not hold any mutable state. However it is important that the source of a `Segment<T>` is, since that is the owner of mutable state. The source is often a `BehaviorSubject`, `MutableLiveData` or a `Emitter` which are all holding state with a lock, so those are thread safe.

It is advised to update the state only from a single thread, since that will create the most predictable behavior. Although it is not said that that single thread has to be the main thread.


# License
   Copyright 2019 Danny Lamarti

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
