package online.dbaltor.cnab.adapter.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import online.dbaltor.cnab.adapter.persistence.TransactionRepository;
import online.dbaltor.cnab.usecase.OperationsManager;
import online.dbaltor.cnab.usecase.OpsManException;

@Controller
@RequiredArgsConstructor
public class UploadFileController {
    private @NonNull OperationsManager operationsManager;
    private @NonNull TransactionRepository transactionRepository;

	@GetMapping("/")
	public String showHomePage(Model model) {
		return "uploadForm";
	}

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) throws IOException {
		
		try {
			operationsManager.parseCNABContent(file.getInputStream())
				.forEach(tx -> transactionRepository.save(tx));
		
			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded " + file.getOriginalFilename() + "!");
		} catch (OpsManException ome) {
			val errorMsg = switch (ome.getErrorType()) {
				case SHOP_NOT_FOUND -> "No shop with the id provided has been found";
				case TXTYPE_NOT_FOUND -> "The file contains a transaction type not configured";
				default -> "Unknown error";
			};
			redirectAttributes.addFlashAttribute("message",
					"Something wrong has happened: \n" + errorMsg);			
		}

		return "redirect:/";
	}
	
}
