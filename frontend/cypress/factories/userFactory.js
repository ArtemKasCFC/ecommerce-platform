import {randomEmail, randomName, randomPassword} from "../generators/userDataGenerator.js";

export function defaultUser() {
    return {
        name: randomName(),
        email: randomEmail(),
        password: randomPassword()
    };
}