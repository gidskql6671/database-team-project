package dto;

public class Department {
	public String code;
	public String name;
	public int totalRequiredCredits;

	public Department(String code, String name, int totalRequiredCredits) {
		this.code = code;
		this.name = name;
		this.totalRequiredCredits = totalRequiredCredits;
	}
}
