export enum CardStatus {
	NEW = 'new',
	LEARN = 'learn',
	KNOWN = 'known',
}

export interface FlashCard {
	_id: string;
	setId: string;
	question: string;
	answer: string;
	status: CardStatus;
	createdAt?: Date;
}