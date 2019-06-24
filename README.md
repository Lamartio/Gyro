# Gyro
Building graphical

- General way of working
- Asynchronous actions
- interops

#Getting started
For understanding why Gyro is 
```kotlin
data class House(val door: Door = Door())

data class Door(val bell: Bell = Bell(), val isOpen: Boolean = false)

data class Bell(val isRinging: Boolean = false)
```
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

The `select` and `filter` operator creates a new `Segment<Door>` and each segment can be considered as a standalone updater of the current state. This allows you to segregate the logic of updating a door from any other part of the House, which is the basis for scalability.

A `Segment` is responsible for maintaining state changes, but UI is only interested in listening to those changes. Therefore a Segment can easily obtained from a observable source such as a Rx's `BehaviorSubject` and `ReplaySubject` or Android's `MutableLiveData`. If you don't want to add additional dependencies; Gyro includes an observable source called `Emitter`.

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
The above examples all create the same functionality. The `Subject`, `LiveData` and `Emitter` distribute state changes  and have operators like `map`, `filter` and `distinctUntilChanged`. All of them have an extension function to create a `Segment`, which is responsible for changing the state. 

Do notice that since `Emitter` is written in Kotlin it has has null-safety. Therefore it is the only one that produces a `Segment` instead of a `OptionalSegment`.

With an observable state and a way of updating the state, we have all the ingredients for building an interactive application. For making the UI as simple as possible, we define an API that is a `Sender` (Gyro's alternative for Rx's `Observable`) and contains the functions necessary for the UI.

```kotlin
interface Actions : Sender<State> {
    fun closeDoor()
    fun openDoor()
}
```
How `Actions` can be distributed through your application can be seen in the sample application of this repo. The importance is that whatever your UI component (Controller, Presenter, ViewModel, Fragment ...) is going to be, it is supplied with the interactions and a observable of state.

```kotlin
class DoorFragment : Fragment() {
    /*...*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val openDoorButton = view.findViewById<Button>(R.id.openDoor)

        openDoorButton.setOnClickListener { actions.openDoor() } // calls interaction

        actions
            .map { house -> !house.door.isOpen }
            .distinctUntilChanged()
            .subscribe { isClosed -> openDoorButton.isEnabled = isClosed } // gets updates
    }
}
```
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

Now the system is in a `SignIn` state and can progress by a call of the `success` or `failure` function. Both of them update the state by setting the `user` property, which will call `Segment.set`.

# Side effects


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
