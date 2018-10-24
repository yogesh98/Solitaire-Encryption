package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;


/**
 * This class implements a simplified version of Bruce Schneier's Solitaire
 * Encryption algorithm.
 *
 * @author RU NB CS112
 */
public class Solitaire {

	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;

	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a
	 * circular linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i = 0; i < cardValues.length; i++) {
			cardValues[i] = i + 1;
		}

		// shuffle the cards
		Random randgen = new Random();
		for (int i = 0; i < cardValues.length; i++) {
			int other = randgen.nextInt(28);
			int temp = cardValues[i];
			cardValues[i] = cardValues[other];
			cardValues[other] = temp;
		}

		// create a circular linked list from this deck and make deckRear point
		// to its last node
		CardNode cn = new CardNode();
		cn.cardValue = cardValues[0];
		cn.next = cn;
		deckRear = cn;
		for (int i = 1; i < cardValues.length; i++) {
			cn = new CardNode();
			cn.cardValue = cardValues[i];
			cn.next = deckRear.next;
			deckRear.next = cn;
			deckRear = cn;
		}
	}

	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
			cn.cardValue = scanner.nextInt();
			cn.next = cn;
			deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
			cn.cardValue = scanner.nextInt();
			cn.next = deckRear.next;
			deckRear.next = cn;
			deckRear = cn;
		}
	}

	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		// COMPLETE THIS METHOD
		CardNode ptr = deckRear.next;
		CardNode nextPtr = deckRear.next.next;
		while (true) {
			if (ptr.cardValue == 27) {
				int temp = nextPtr.cardValue;
				nextPtr.cardValue = ptr.cardValue;
				ptr.cardValue = temp;
				return;
			}
			ptr = ptr.next;
			nextPtr = nextPtr.next;
		}
	}

	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
		// COMPLETE THIS METHOD
		CardNode ptr = deckRear.next;
		CardNode prev = deckRear;
		CardNode nextPtr = deckRear.next.next;
		int count = 0;
		while (true) {
			if (ptr.cardValue == 28) {
				int temp = nextPtr.cardValue;
				nextPtr.cardValue = ptr.cardValue;
				ptr.cardValue = temp;
				count++;
				if (count == 2)
					return;
			}
			prev = prev.next;
			ptr = ptr.next;
			nextPtr = nextPtr.next;
		}
	}

	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		// COMPLETE THIS METHOD
		if (deckRear == null) {
			return;
		}

		CardNode ptr = deckRear, jokerA = null, jokerB = null;
		int firstValue, secondValue;
		if ((ptr.cardValue == 27 || ptr.cardValue == 28) && (ptr.next.cardValue == 27 || ptr.next.cardValue == 28)) {
			return;
		} else if (ptr.next.cardValue == 27 || ptr.next.cardValue == 28) {
			jokerA = ptr.next;
			firstValue = jokerA.cardValue;
			if (firstValue == 27)
				secondValue = 28;
			else {
				secondValue = 27;
			}
			do {
				ptr = ptr.next;
			} while (ptr.cardValue != secondValue);
			deckRear = ptr;
			return;
		} else if (ptr.cardValue == 27 || ptr.cardValue == 28) {
			secondValue = ptr.cardValue;
			if (secondValue == 28)
				firstValue = 27;
			else {
				firstValue = 28;
			}
			ptr = deckRear;
			do {
				ptr = ptr.next;
			} while (ptr.next.cardValue != firstValue);
			deckRear = ptr;
			return;
		}
		CardNode jokerBAfter = null, jokerABefore = null;
		do {
			if (ptr.next.cardValue == 28 || ptr.next.cardValue == 27) {
				jokerABefore = ptr;
				jokerA = ptr.next;
				break;
			}
			ptr = ptr.next;
		} while (ptr != deckRear);
		ptr = jokerA.next;
		do {
			if (ptr.cardValue == 28 || ptr.cardValue == 27) {
				jokerB = ptr;
				jokerBAfter = ptr.next;
				break;
			}
			ptr = ptr.next;
		} while (ptr != jokerA);
		jokerABefore.next = jokerBAfter;
		jokerB.next = deckRear.next;
		deckRear.next = jokerA;
		deckRear = jokerABefore;
		return;
	}

	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {
		// COMPLETE THIS METHOD
		if (deckRear == null) {
			return;
		} else if (deckRear == deckRear.next) {
			System.out.println("Only one element cannot cut");
			return;
		}

		int lastValue;
		int count = 0;

		if (deckRear.cardValue == 28) {
			lastValue = 27;
		} else {
			lastValue = deckRear.cardValue;
		}

		CardNode stopCard = null;
		CardNode prev = null, ptr = deckRear;
		do {
			prev = ptr;
			ptr = ptr.next;
		} while (ptr != deckRear);
		prev.next = deckRear.next;
		prev = deckRear;

		CardNode prev2 = deckRear;
		for (count = 0; count < lastValue; count++) {
			prev2 = prev2.next;
		}
		CardNode lastNode = new CardNode();
		lastNode.cardValue = deckRear.cardValue;
		lastNode.next = prev2.next;
		prev2.next = lastNode;
		deckRear = lastNode;

		return;
	}

	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count
	 * Cut, then counts down based on the value of the first card and extracts
	 * the next card value as key. But if that value is 27 or 28, repeats the
	 * whole process (Joker A through Count Cut) on the latest (current) deck,
	 * until a value less than or equal to 26 is found, which is then returned.
	 *
	 * @return Key between 1 and 26
	 */
	int getKey() {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		int key, value;
		do {
			jokerA();
			jokerB();
			tripleCut();
			countCut();
			CardNode ptr = deckRear.next;

			if (deckRear.next.cardValue == 28)
				value = 27;
			else {
				value = deckRear.next.cardValue;
			}
			if (value == 28) {
				value = 27;
			}
			for (int i = 1; i < value; i++) {
				ptr = ptr.next;
			}
			key = ptr.next.cardValue;
		} while (key > 26);
		return key;
	}

	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 *
	 * @param rear
	 *            Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) {
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 *
	 * @param message
	 *            Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		String encryptedOutput = "", formatted = "";
		// Remove unwanted characters
		// Save only letters to a different string
		for (int i = 0; i < message.length(); i++) {
			if (Character.isLetter(message.charAt(i))) {
				formatted += message.charAt(i);
			}
		}
		formatted = formatted.toUpperCase();

		for (int i = 0; i < formatted.length(); i++) {
			// Encrypt formatted
			int sum;
			int key = getKey();
			sum = (formatted.charAt(i) - 'A' + 1) + key;
			if (sum > 26)
				sum -= 26;

			char modifiedLetter = (char) (sum - 1 + 'A');
			encryptedOutput += modifiedLetter;
		}

		return encryptedOutput;

	}

	/**
	 * Decrypts a message, which consists of upper case letters only
	 *
	 * @param message
	 *            Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE

		String decryptedInput = "";
		int key, num;

		for (int i = 0; i < message.length(); i++) {
			num = (message.charAt(i)) - 'A' + 1;
			key = getKey();
			System.out.println("Key " + key);
			if (num <= key) {
				num += 26;
			}

			char modifiedLetter = (char) ((num - key) - 1 + 'A');
			decryptedInput += modifiedLetter;
		}
		return decryptedInput;

	}
}
