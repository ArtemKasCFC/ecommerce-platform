import {faker} from "@faker-js/faker";

export function randomName() {
    return faker.person.fullName();
}

export function randomEmail() {
    return faker.internet.email();
}

export function randomPassword() {
    return `Aa1!${faker.string.alphanumeric(8)}`;
}