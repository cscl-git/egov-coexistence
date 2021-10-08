ClassicEditor
	.create( document.querySelector( '#editor' ), {
		toolbar: {
			items: [
				'heading',
				'|',
				'fontFamily',
				'fontSize',
				'bold',
				'italic',
				'underline',
				'fontColor',
				'fontBackgroundColor',						
				'highlight',	
				'|',						
				'bulletedList',						
				'numberedList',
				'insertTable',						
				'indent',
				'outdent',
				'alignment',
				'pageBreak',
				'|',
				'link',
				'blockQuote',							
				'horizontalLine',						
				'strikethrough',						
				'superscript',						
				'subscript',
				'specialCharacters',
				'|',
				'undo',
				'redo',
				'removeFormat'
				
			]
		},
		language: {
			ui: 'en',
			content: 'en'
		},
		fontSize: {
			options: [
				9,
				11,
				13,
				'default',
				17,
				19,
				21
			]
		},
		alignment: {
			options: [ 'left', 'right','center','justify' ]
		},
		image: {
			toolbar: [
				'imageTextAlternative',
				'imageStyle:full',
				'imageStyle:side'
			]
		},
		table: {
			contentToolbar: [
				'tableColumn',
				'tableRow',
				'mergeTableCells',
				'tableCellProperties',
				'tableProperties'
			]
		},
		typing: {
			transformations: {
				remove: [
					// Do not use the transformations from the
					// 'symbols' and 'quotes' groups.
					'symbols',
					'quotes',

					// As well as the following transformations.
					'arrowLeft',
					'arrowRight'
				],

				extra: [
					// Add some custom transformations – e.g. for emojis.
					{ from: ':)', to: '🙂' },
					{ from: ':+1:', to: '👍' },
					{ from: ':tada:', to: '🎉' },

					// You can also define patterns using regular expressions.
					// Note: The pattern must end with `$` and all its fragments must be wrapped
					// with capturing groups.
					// The following rule replaces ` "foo"` with ` «foo»`.
					{
						from: /(^|\s)(")([^"]*)(")$/,
						to: [ null, '«', null, '»' ]
					},

					// Finally, you can define `to` as a callback.
					// This (naive) rule will auto-capitalize the first word after a period.
					{
						from: /(\. )([a-z])$/,
						to: matches => [ null, matches[ 1 ].toUpperCase() ]
					}
				],
			}
		},
		licenseKey: '',
		
	} )
	.then( editor => {
		window.editor = editor;				
	} )
	.catch( error => {
		console.error( 'Oops, something gone wrong!' );
		console.error( error );
	} );