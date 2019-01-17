function decorate(request, response) {
	const fs = require('fs');
	let meta = '\r\n\r\n' + new Date().toISOString() + '\r\n';
	fs.appendFileSync('/mountebank/response.log', meta + response.body, 'utf-8');

	try {
		var json = JSON.parse(response.body);
		delete json.link;
		json.book = json.book.replace(new RegExp('Pussy|Pooh|Shit'), '***');

		response.body = JSON.stringify(json);
	} catch (err) {
		console.error('Could not parse response:', err);
	}
}